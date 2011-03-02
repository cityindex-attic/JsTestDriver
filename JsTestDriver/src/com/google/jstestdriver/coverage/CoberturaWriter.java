package com.google.jstestdriver.coverage;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class CoberturaWriter implements CoverageWriter {

  private final Writer out;
  private String currentFileName = "";
  private String currentPackageName = "";
  private final CoverageNameMapper mapper;
  private Map<String, PackageData> packages = new TreeMap<String, PackageData>();
  private ClassData currentClassData;
  private PackageData currentPackageData;
  private CoverageData currentProjectData;
  private File basePath;

  @Inject
  public CoberturaWriter(@Named("coverageFileWriter") Writer out,
                         CoverageNameMapper mapper, File basePath) {
    this.out = out;
    this.mapper = mapper;
    this.basePath = basePath;
    currentProjectData = new CoverageData();
  }

  public void writeRecordStart(Integer fileId) {
    currentFileName = mapper.unmap(fileId);
    String packageName = calculatePackageName(currentFileName);
    if (!currentPackageName.equals(packageName)) {
      if (currentPackageData != null) {
        packages.put(currentPackageName, currentPackageData);
      }
      currentPackageName = packageName;
      currentPackageData = packages.get(currentPackageName);
      if (currentPackageData == null) {
        currentPackageData = new PackageData();
      }
    }
    currentClassData = currentPackageData.getClassData(currentFileName);
    if (currentClassData == null) {
      currentClassData = new ClassData();
    }
  }

  public void writeRecordEnd() {
    currentPackageData.addClassData(currentFileName, currentClassData);
  }

  private String calculatePackageName(String qualifiedFile) {
    return new File(qualifiedFile).getParent();
  }

  public void writeCoverage(int lineNumber, int executedNumber) {
    currentClassData.addLineData(lineNumber, executedNumber);
  }

  public void flush() {
    // put final package data
    packages.put(currentPackageName, currentPackageData);

    // now that we know all packages are done, aggregate the totals for the project
    for (PackageData data : packages.values()) {
      currentProjectData.aggregate(data.getCoverage());
    }

    writeCoverageFile();
  }

  public void writeCoverageFile() {
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();

      // should there be a DOCTYPE? <!DOCTYPE coverage SYSTEM \"http://cobertura.sourceforge.net/xml/coverage-04.dtd">

      Element coverage = doc.createElement("coverage");
      doc.appendChild(coverage);
      coverage.setAttribute("line-rate", currentProjectData.getLineRate());
      coverage.setAttribute("branch-rate", currentProjectData.getBranchRate());
      coverage.setAttribute("lines-covered", currentProjectData.getExecutions());
      coverage.setAttribute("lines-valid", currentProjectData.getTotalLines());
      coverage.setAttribute("branches-covered", "0");
      coverage.setAttribute("branches-valid", "0");
      coverage.setAttribute("complexity", "0");
      coverage.setAttribute("version", "1.9"); // do we need to keep this in sync?
      coverage.setAttribute("timestamp", String.valueOf(new Date().getTime()));

      Element sources = doc.createElement("sources");
      coverage.appendChild(sources);
      Element source = doc.createElement("source");
      sources.appendChild(source);
      source.setTextContent(basePath.getAbsolutePath());

      Element packagesElement = doc.createElement("packages");
      for (Map.Entry<String, PackageData> packageEntry : packages.entrySet()) {
        String packageName = packageEntry.getKey();
        CoverageData packageCoverageData = packageEntry.getValue().getCoverage();
        Element packageElement = doc.createElement("package");
        packageElement.setAttribute("name", packageName);
        packageElement.setAttribute("line-rate", packageCoverageData.getLineRate());
        packageElement.setAttribute("branch-rate", packageCoverageData.getBranchRate());
        packageElement.setAttribute("complexity", "0");
        packagesElement.appendChild(packageElement);

        Element classesElement = doc.createElement("classes");

        for (Map.Entry<String, ClassData> classEntry : packageEntry.getValue().getClasses().entrySet()) {
          String className = classEntry.getKey();
          CoverageData classCoverageData = classEntry.getValue().getCoverage();

          Element classElement = doc.createElement("class");
          classElement.setAttribute("name", className);
          classElement.setAttribute("filename", className);
          classElement.setAttribute("line-rate", classCoverageData.getLineRate());
          classElement.setAttribute("branch-rate", classCoverageData.getBranchRate());
          classElement.setAttribute("complexity", "0");
          classesElement.appendChild(classElement);

          Element linesElement = doc.createElement("lines");

          for (Map.Entry<Integer, CoverageData> lineEntry : classEntry.getValue().getLines().entrySet()) {
            CoverageData data3 = lineEntry.getValue();

            Element line = doc.createElement("line");
            line.setAttribute("number", String.valueOf(lineEntry.getKey()));
            line.setAttribute("hits", data3.getExecutions());
            line.setAttribute("branch", "0");
            linesElement.appendChild(line);
          }
          classElement.appendChild(linesElement);

        }
        packageElement.appendChild(classesElement);

      }
      coverage.appendChild(packagesElement);
      
      //write the content into xml file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource domSource = new DOMSource(doc);

      // set some options on the transformer
      transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      // get a transformer and supporting classes
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);

      // transform the xml document into a string
      transformer.transform(domSource, result);

      // write the output file
      out.write(writer.toString());
      out.close();

    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (TransformerException tfe) {
      tfe.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    } finally {
    }

  }

  private class CoverageData {
    private int totalLines = 0;
    private int executed = 0;

    public CoverageData() {
    }

    public CoverageData(int totalLines, int executed) {
      this.totalLines = totalLines;
      this.executed = executed;
    }

    public String getLineRate() {
      return String.valueOf((float)executed / (float)totalLines);
    }

    public String getBranchRate() {
      return "0";
    }

    public String getTotalLines() {
      return String.valueOf(totalLines);
    }

    public void aggregate(CoverageData data) {
      totalLines += data.totalLines;
      executed += data.executed;
    }

    public String getExecutions() {
      return String.valueOf(executed);
    }

    @Override
    public String toString() {
      return "CoverageData{" +
        "totalLines=" + totalLines +
        ", executed=" + executed +
        '}';
    }
  }

  private class ClassData {
    public CoverageData coverage = new CoverageData();

    // key is line number, value is # of executions 
    public SortedMap<Integer, CoverageData> lines = new TreeMap<Integer, CoverageData>();

    public SortedMap<Integer, CoverageData> getLines() {
      return lines;
    }

    public CoverageData getCoverage() {
      return coverage;
    }

    public void addLineData(int lineNumber, int executions) {
      CoverageData data = new CoverageData(1, executions > 0 ? 1 : 0);
      lines.put(lineNumber, data);
      coverage.aggregate(data);
    }

    @Override
    public String toString() {
      return "ClassData{" +
        "coverage=" + coverage +
        ", lines=" + lines +
        '}';
    }
  }

  private class PackageData {
    CoverageData coverage = new CoverageData();
    public Map<String, ClassData> classes = new HashMap<String, ClassData>();
    public Map<String, ClassData> getClasses() {
      return classes;
    }
    public void addClassData(String className, ClassData data) {
      classes.put(className, data);
      coverage.aggregate(data.getCoverage());
    }

    public CoverageData getCoverage() {
      return coverage;
    }

    public ClassData getClassData(String className) {
      return classes.get(className);
    }

    @Override
    public String toString() {
      return "PackageData{" +
        "coverage=" + coverage +
        ", classes=" + classes +
        '}';
    }
  }
}