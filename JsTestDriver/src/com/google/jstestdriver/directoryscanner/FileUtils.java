/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.google.jstestdriver.directoryscanner;

import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * This class also encapsulates methods which allow Files to be
 * referred to using abstract path names which are translated to native
 * system file paths at runtime as well as copying files or setting
 * their last modification time.
 *
 */
public class FileUtils {
    private static final FileUtils PRIMARY_INSTANCE = new FileUtils();

    private static final boolean ON_NETWARE = Os.isFamily("netware");
    private static final boolean ON_DOS = Os.isFamily("dos");

    static final int BUF_SIZE = 8192;


    /**
     * The granularity of timestamps under FAT.
     */
    public static final long FAT_FILE_TIMESTAMP_GRANULARITY = 2000;

    /**
     * The granularity of timestamps under Unix.
     */
    public static final long UNIX_FILE_TIMESTAMP_GRANULARITY = 1000;

    /**
     * The granularity of timestamps under the NT File System.
     * NTFS has a granularity of 100 nanoseconds, which is less
     * than 1 millisecond, so we round this up to 1 millisecond.
     */
    public static final long NTFS_FILE_TIMESTAMP_GRANULARITY = 1;

    /**
     * Method to retrieve The FileUtils, which is shared by all users of this
     * method.
     * @return an instance of FileUtils.
     * @since Ant 1.6.3
     */
    public static FileUtils getFileUtils() {
        return PRIMARY_INSTANCE;
    }

    /**
     * Empty constructor.
     */
    protected FileUtils() {
    }

    /**
     * Verifies that the specified filename represents an absolute path.
     * Differs from new java.io.File("filename").isAbsolute() in that a path
     * beginning with a double file separator--signifying a Windows UNC--must
     * at minimum match "\\a\b" to be considered an absolute path.
     * @param filename the filename to be checked.
     * @return true if the filename represents an absolute path.
     * @throws java.lang.NullPointerException if filename is null.
     * @since Ant 1.6.3
     */
    public static boolean isAbsolutePath(String filename) {
        int len = filename.length();
        if (len == 0) {
            return false;
        }
        char sep = File.separatorChar;
        filename = filename.replace('/', sep).replace('\\', sep);
        char c = filename.charAt(0);
        if (!(ON_DOS || ON_NETWARE)) {
            return (c == sep);
        }
        if (c == sep) {
            // CheckStyle:MagicNumber OFF
            if (!(ON_DOS && len > 4 && filename.charAt(1) == sep)) {
                return false;
            }
            // CheckStyle:MagicNumber ON
            int nextsep = filename.indexOf(sep, 2);
            return nextsep > 2 && nextsep + 1 < len;
        }
        int colon = filename.indexOf(':');
        return (Character.isLetter(c) && colon == 1
                && filename.length() > 2 && filename.charAt(2) == sep)
                || (ON_NETWARE && colon > 0);
    }

    /**
     * &quot;Normalize&quot; the given absolute path.
     *
     * <p>This includes:
     * <ul>
     *   <li>Uppercase the drive letter if there is one.</li>
     *   <li>Remove redundant slashes after the drive spec.</li>
     *   <li>Resolve all ./, .\, ../ and ..\ sequences.</li>
     *   <li>DOS style paths that start with a drive letter will have
     *     \ as the separator.</li>
     * </ul>
     * Unlike {@link File#getCanonicalPath()} this method
     * specifically does not resolve symbolic links.
     *
     * @param path the path to be normalized.
     * @return the normalized version of the path.
     *
     * @throws java.lang.NullPointerException if path is null.
     */
    public File normalize(final String path) {
        Stack s = new Stack();
        String[] dissect = dissect(path);
        s.push(dissect[0]);

        StringTokenizer tok = new StringTokenizer(dissect[1], File.separator);
        while (tok.hasMoreTokens()) {
            String thisToken = tok.nextToken();
            if (".".equals(thisToken)) {
                continue;
            }
            if ("..".equals(thisToken)) {
                if (s.size() < 2) {
                    // Cannot resolve it, so skip it.
                    return new File(path);
                }
                s.pop();
            } else { // plain component
                s.push(thisToken);
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.size(); i++) {
            if (i > 1) {
                // not before the filesystem root and not after it, since root
                // already contains one
                sb.append(File.separatorChar);
            }
            sb.append(s.elementAt(i));
        }
        return new File(sb.toString());
    }

    /**
     * Dissect the specified absolute path.
     * @param path the path to dissect.
     * @return String[] {root, remaining path}.
     * @throws java.lang.NullPointerException if path is null.
     * @since Ant 1.7
     */
    public String[] dissect(String path) {
        char sep = File.separatorChar;
        path = path.replace('/', sep).replace('\\', sep);

        // make sure we are dealing with an absolute path
        if (!isAbsolutePath(path)) {
            throw new DirectoryScannerException(path + " is not an absolute path");
        }
        String root = null;
        int colon = path.indexOf(':');
        if (colon > 0 && (ON_DOS || ON_NETWARE)) {

            int next = colon + 1;
            root = path.substring(0, next);
            char[] ca = path.toCharArray();
            root += sep;
            //remove the initial separator; the root has it.
            next = (ca[next] == sep) ? next + 1 : next;

            StringBuffer sbPath = new StringBuffer();
            // Eliminate consecutive slashes after the drive spec:
            for (int i = next; i < ca.length; i++) {
                if (ca[i] != sep || ca[i - 1] != sep) {
                    sbPath.append(ca[i]);
                }
            }
            path = sbPath.toString();
        } else if (path.length() > 1 && path.charAt(1) == sep) {
            // UNC drive
            int nextsep = path.indexOf(sep, 2);
            nextsep = path.indexOf(sep, nextsep + 1);
            root = (nextsep > 2) ? path.substring(0, nextsep + 1) : path;
            path = path.substring(root.length());
        } else {
            root = File.separator;
            path = path.substring(1);
        }
        return new String[] {root, path};
    }

    /**
     * Checks whether a given file is a symbolic link.
     *
     * <p>It doesn't really test for symbolic links but whether the
     * canonical and absolute paths of the file are identical--this
     * may lead to false positives on some platforms.</p>
     *
     * @param parent the parent directory of the file to test
     * @param name the name of the file to test.
     *
     * @return true if the file is a symbolic link.
     * @throws IOException on error.
     * @since Ant 1.5
     */
    public boolean isSymbolicLink(File parent, String name)
        throws IOException {
        if (parent == null) {
            File f = new File(name);
            parent = f.getParentFile();
            name = f.getName();
        }
        File toTest = new File(parent.getCanonicalPath(), name);
        return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
    }

    /**
     * Removes a leading path from a second path.
     *
     * @param leading The leading path, must not be null, must be absolute.
     * @param path The path to remove from, must not be null, must be absolute.
     *
     * @return path's normalized absolute if it doesn't start with
     * leading; path's path with leading's path removed otherwise.
     *
     * @since Ant 1.5
     */
    public String removeLeadingPath(File leading, File path) {
        String l = normalize(leading.getAbsolutePath()).getAbsolutePath();
        String p = normalize(path.getAbsolutePath()).getAbsolutePath();
        if (l.equals(p)) {
            return "";
        }
        // ensure that l ends with a /
        // so we never think /foo was a parent directory of /foobar
        if (!l.endsWith(File.separator)) {
            l += File.separator;
        }
        return (p.startsWith(l)) ? p.substring(l.length()) : p;
    }
}
