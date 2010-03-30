jstestdriver.RESPONSE_TYPES={FILE_LOAD_RESULT:"FILE_LOAD_RESULT",REGISTER_RESULT:"REGISTER_RESULT",TEST_RESULT:"TEST_RESULT",TEST_QUERY_RESULT:"TEST_QUERY_RESULT",RESET_RESULT:"RESET_RESULT",COMMAND_RESULT:"COMMAND_RESULT"};jstestdriver.Response=function(c,a,b){this.type=c;this.response=a;this.browser=b};jstestdriver.CommandResponse=function(a,b){this.done=a;this.response=b};jstestdriver.BrowserInfo=function(a){this.id=a};function expectAsserts(a){jstestdriver.expectedAssertCount=a}function fail(b){var a=new Error(b);a.name="AssertError";throw a}function isBoolean_(a){if(typeof(a)!="boolean"){this.fail("Not a boolean: "+this.prettyPrintEntity_(a))}}function prettyPrintEntity_(a){var b=JSON.stringify(a);if(!b){if(typeof a==="function"){return"[function]"}return"["+typeof a+"]"}return b}function argsWithOptionalMsg_(b,e){var a=[];for(var d=0;d<b.length;d++){a.push(b[d])}var c=e-1;if(b.length<c){this.fail("expected at least "+c+" arguments, got "+b.length)}else{if(b.length==e){a[0]+=" "}else{a.unshift("")}}return a}function assertTrue(b,c){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;isBoolean_(a[1]);if(a[1]!=true){this.fail(a[0]+"expected true but was "+this.prettyPrintEntity_(a[1]))}return true}function assertFalse(b,c){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;isBoolean_(a[1]);if(a[1]!=false){this.fail(a[0]+"expected false but was "+this.prettyPrintEntity_(a[1]))}return true}function assertEquals(c,b,d){var a=this.argsWithOptionalMsg_(arguments,3);jstestdriver.assertCount++;c=a[0];b=a[1];d=a[2];if(!compare_(b,d)){this.fail(c+"expected "+this.prettyPrintEntity_(b)+" but was "+this.prettyPrintEntity_(d)+"")}return true}function compare_(d,e){var b=null;if(d!==undefined&&typeof(d)=="object"){var c=0;var a=0;if(d instanceof [].constructor){c=e.length}else{for(b in e){if(!e.hasOwnProperty(b)){continue}++c}}for(b in d){if(!d.hasOwnProperty(b)){continue}if(!compare_(d[b],e[b])){return false}++a}if(a!==c){return false}return true}if(e!=d){return false}return true}function assertNotEquals(d,b,f){try{assertEquals.apply(this,arguments)}catch(c){if(c.name=="AssertError"){return true}throw c}var a=this.argsWithOptionalMsg_(arguments,3);this.fail(a[0]+"expected "+this.prettyPrintEntity_(a[1])+" not to be equal to "+this.prettyPrintEntity_(a[2]))}function assertSame(c,b,d){var a=this.argsWithOptionalMsg_(arguments,3);jstestdriver.assertCount++;if(!isSame_(a[2],a[1])){this.fail(a[0]+"expected "+this.prettyPrintEntity_(a[1])+" but was "+this.prettyPrintEntity_(a[2]))}return true}function assertNotSame(c,b,d){var a=this.argsWithOptionalMsg_(arguments,3);jstestdriver.assertCount++;if(isSame_(a[2],a[1])){this.fail(a[0]+"expected not same as "+this.prettyPrintEntity_(a[1])+" but was "+this.prettyPrintEntity_(a[2]))}return true}function isSame_(a,b){return b===a}function assertNull(b,c){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;if(a[1]!==null){this.fail(a[0]+"expected null but was "+this.prettyPrintEntity_(a[1]))}return true}function assertNotNull(b,c){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;if(a[1]===null){this.fail(a[0]+"expected not null but was null")}return true}function assertUndefined(b,c){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;if(typeof a[1]!="undefined"){this.fail(a[2]+"expected undefined but was "+this.prettyPrintEntity_(a[1]))}return true}function assertNotUndefined(b,c){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;if(typeof a[1]=="undefined"){this.fail(a[0]+"expected not undefined but was undefined")}return true}function assertNaN(b,c){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;if(!isNaN(a[1])){this.fail(a[0]+"expected to be NaN but was "+a[1])}return true}function assertNotNaN(b,c){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;if(isNaN(a[1])){this.fail(a[0]+"expected not to be NaN")}return true}function assertException(c,d,a){if(arguments.length==1){d=c;c=""}else{if(arguments.length==2){if(typeof d!="function"){a=d;d=c;c=""}else{c+=" "}}else{c+=" "}}jstestdriver.assertCount++;try{d()}catch(b){if(b.name=="AssertError"){throw b}if(a&&b.name!=a){this.fail(c+"expected to throw "+a+" but threw "+b.name)}return true}this.fail(c+"expected to throw exception")}function assertNoException(c,d){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;try{a[1]()}catch(b){fail(a[0]+"expected not to throw exception, but threw "+b.name+" ("+b.message+")")}}function assertArray(b,c){var a=this.argsWithOptionalMsg_(arguments,2);jstestdriver.assertCount++;if(!jstestdriver.jQuery.isArray(a[1])){fail(a[0]+"expected to be array, but was "+this.prettyPrintEntity_(a[1]))}}function assertTypeOf(d,b,c){var a=this.argsWithOptionalMsg_(arguments,3);jstestdriver.assertCount++;var e=typeof a[2];if(e!=a[1]){this.fail(a[0]+"expected to be "+a[1]+" but was "+e)}return true}function assertBoolean(b,c){var a=this.argsWithOptionalMsg_(arguments,2);return assertTypeOf(a[0],"boolean",a[1])}function assertFunction(b,c){var a=this.argsWithOptionalMsg_(arguments,2);return assertTypeOf(a[0],"function",a[1])}function assertObject(b,c){var a=this.argsWithOptionalMsg_(arguments,2);return assertTypeOf(a[0],"object",a[1])}function assertNumber(b,c){var a=this.argsWithOptionalMsg_(arguments,2);return assertTypeOf(a[0],"number",a[1])}function assertString(b,c){var a=this.argsWithOptionalMsg_(arguments,2);return assertTypeOf(a[0],"string",a[1])}function assertMatch(d,c,e){var a=this.argsWithOptionalMsg_(arguments,3);var b=typeof a[2]=="undefined";jstestdriver.assertCount++;if(b||!a[1].test(a[2])){e=(b?undefined:this.prettyPrintEntity_(a[2]));this.fail(a[0]+"expected "+e+" to match "+a[1])}return true}function assertNoMatch(c,b,d){var a=this.argsWithOptionalMsg_(arguments,3);jstestdriver.assertCount++;if(a[1].test(a[2])){this.fail(a[0]+"expected "+this.prettyPrintEntity_(a[2])+" not to match "+a[1])}return true}function assertTagName(d,c,b){var a=this.argsWithOptionalMsg_(arguments,3);var e=a[2]&&a[2].tagName;if(String(e).toUpperCase()!=a[1].toUpperCase()){this.fail(a[0]+"expected tagName to be "+a[1]+" but was "+e)}return true}function assertClassName(g,c,b){var a=this.argsWithOptionalMsg_(arguments,3);var h=a[2]&&a[2].className;var d=new RegExp("(^|\\s)"+a[1]+"(\\s|$)");try{this.assertMatch(a[0],d,h)}catch(f){h=this.prettyPrintEntity_(h);this.fail(a[0]+"expected class name to include "+this.prettyPrintEntity_(a[1])+" but was "+h)}return true}function assertElementId(c,e,b){var a=this.argsWithOptionalMsg_(arguments,3);var d=a[2]&&a[2].id;jstestdriver.assertCount++;if(d!==a[1]){this.fail(a[0]+"expected id to be "+a[1]+" but was "+d)}return true}function assertInstanceOf(e,c,f){var a=this.argsWithOptionalMsg_(arguments,3);jstestdriver.assertCount++;if(!(Object(a[2]) instanceof a[1])){var d=a[1]&&a[1].name||a[1];var b=this.prettyPrintEntity_(a[2]);this.fail(a[0]+"expected "+b+" to be instance of "+d)}return true}function assertNotInstanceOf(e,c,f){var a=this.argsWithOptionalMsg_(arguments,3);jstestdriver.assertCount++;if(Object(a[2]) instanceof a[1]){var d=a[1]&&a[1].name||a[1];var b=this.prettyPrintEntity_(a[2]);this.fail(a[0]+"expected "+b+" not to be instance of "+d)}return true}var assert=assertTrue;jstestdriver.FileSource=function(b,a,c){this.fileSrc=b;this.timestamp=a;this.basePath=c};jstestdriver.FileResult=function(a,c,b){this.file=a;this.success=c;this.message=b};jstestdriver.formatString=function(){var d=arguments.length;var a=[];var g=new RegExp("%[sdifo]","g");var e=0;while(e<d){var k=arguments[e];var h=g.exec(k);if(!h){var j=k;if(typeof j=="object"){j=JSON.stringify(j)}if(a.length>0){a.push(" ")}a.push(j);e++}else{var c=1;var b=e+1;var f=k;do{var j=arguments[b++];if(typeof j=="object"){j=JSON.stringify(j)}f=f.replace(h,j);c++}while((h=g.exec(f))!=null);a.push(f);e+=c}}return a.join("")};jstestdriver.convertToJson=function(a){return function(b,e,f){var d={};for(var c in e){d[c]=JSON.stringify(e[c])}a(b,d,f)}};jstestdriver.bind=function(a,b){return function(){return b.apply(a,arguments)}};jstestdriver.extractId=function(a){return a.match(/\/(slave|runner)\/(\d+)\//)[2]};jstestdriver.getBrowserFriendlyName=function(){if(jstestdriver.jQuery.browser.safari){if(navigator.userAgent.indexOf("Chrome")!=-1){return"Chrome"}return"Safari"}else{if(jstestdriver.jQuery.browser.opera){return"Opera"}else{if(jstestdriver.jQuery.browser.msie){return"Internet Explorer"}else{if(jstestdriver.jQuery.browser.mozilla){if(navigator.userAgent.indexOf("Firefox")!=-1){return"Firefox"}return"Mozilla"}}}}};jstestdriver.getBrowserFriendlyVersion=function(){if(jstestdriver.jQuery.browser.msie){if(typeof XDomainRequest!="undefined"){return"8.0"}}else{if(jstestdriver.jQuery.browser.safari){if(navigator.appVersion.indexOf("Chrome/")!=-1){return navigator.appVersion.match(/Chrome\/(.*)\s/)[1]}}}return jstestdriver.jQuery.browser.version};jstestdriver.toHtml=function(a,b){return jstestdriver.jQuery(a,b)[0]};jstestdriver.appendHtml=function(b,c){var a=jstestdriver.toHtml(b,c);jstestdriver.jQuery(c.body).append(a)};jstestdriver.Console=function(){this.log_=[]};jstestdriver.Console.prototype.log=function(){this.log_.push("[LOG] "+jstestdriver.formatString.apply(this,arguments))};jstestdriver.Console.prototype.debug=function(){this.log_.push("[DEBUG] "+jstestdriver.formatString.apply(this,arguments))};jstestdriver.Console.prototype.info=function(){this.log_.push("[INFO] "+jstestdriver.formatString.apply(this,arguments))};jstestdriver.Console.prototype.warn=function(){this.log_.push("[WARN] "+jstestdriver.formatString.apply(this,arguments))};jstestdriver.Console.prototype.error=function(){this.log_.push("[ERROR] "+jstestdriver.formatString.apply(this,arguments))};jstestdriver.Console.prototype.getLog=function(){var a=this.log_;this.log_=[];return a.join("\n")};jstestdriver.PluginRegistrar=function(){this.plugins_=[]};jstestdriver.PluginRegistrar.PROCESS_TEST_RESULT="processTestResult";jstestdriver.PluginRegistrar.LOAD_SOURCE="loadSource";jstestdriver.PluginRegistrar.RUN_TEST="runTestConfiguration";jstestdriver.PluginRegistrar.IS_FAILURE="isFailure";jstestdriver.PluginRegistrar.GET_TEST_RUN_CONFIGURATIONS="getTestRunsConfigurationFor";jstestdriver.PluginRegistrar.prototype.register=function(c){var a=this.getIndexOfPlugin_(c.name);var b=1;if(a==-1){a=this.plugins_.length-1;b=0}this.plugins_.splice(a,b,c)};jstestdriver.PluginRegistrar.prototype.unregister=function(b){var a=this.getIndexOfPlugin_(b.name);if(a!=-1){this.plugins_.splice(a,1)}};jstestdriver.PluginRegistrar.prototype.getPlugin=function(b){var a=this.getIndexOfPlugin_(b);return a!=-1?this.plugins_[a]:null};jstestdriver.PluginRegistrar.prototype.getNumberOfRegisteredPlugins=function(){return this.plugins_.length};jstestdriver.PluginRegistrar.prototype.dispatch_=function(e,d){var b=this.plugins_.length;for(var a=0;a<b;a++){var c=this.plugins_[a];if(c[e]){if(c[e].apply(c,d)){return true}}}return false};jstestdriver.PluginRegistrar.prototype.getIndexOfPlugin_=function(a){var c=this.plugins_.length;for(var b=0;b<c;b++){var d=this.plugins_[b];if(d.name==a){return b}}return -1};jstestdriver.PluginRegistrar.prototype.loadSource=function(b,a){this.dispatch_(jstestdriver.PluginRegistrar.LOAD_SOURCE,arguments)};jstestdriver.PluginRegistrar.prototype.runTestConfiguration=function(a,b,c){this.dispatch_(jstestdriver.PluginRegistrar.RUN_TEST,arguments)};jstestdriver.PluginRegistrar.prototype.processTestResult=function(a){this.dispatch_(jstestdriver.PluginRegistrar.PROCESS_TEST_RESULT,arguments)};jstestdriver.PluginRegistrar.prototype.isFailure=function(a){return this.dispatch_(jstestdriver.PluginRegistrar.IS_FAILURE,arguments)};jstestdriver.PluginRegistrar.prototype.getTestRunsConfigurationFor=function(c,b,a){return this.dispatch_(jstestdriver.PluginRegistrar.GET_TEST_RUN_CONFIGURATIONS,arguments)};jstestdriver.LibLoader=function(b,c,a){this.files_=b;this.dom_=c;this.getScript_=a;this.remainingLibToLoad_=this.files_.length;this.boundOnLibLoaded_=jstestdriver.bind(this,this.onLibLoaded);this.savedDocumentWrite_=c.write;this.currentFile_=0};jstestdriver.LibLoader.prototype.load=function(a,b){if(this.files_.length==0){a(b)}else{this.dom_.write=function(){};this.onAllLibLoaded_=a;this.data_=b;this.getScript_(this.dom_,this.files_[this.currentFile_++],this.boundOnLibLoaded_)}};jstestdriver.LibLoader.prototype.onLibLoaded=function(){if(--this.remainingLibToLoad_==0){var a=this.onAllLibLoaded_;var b=this.data_;this.onAllLibLoaded_=null;this.data_=null;this.dom_.write=this.savedDocumentWrite_;a(b)}else{this.getScript_(this.dom_,this.files_[this.currentFile_++],this.boundOnLibLoaded_)}};jstestdriver.FileLoader=function(a,b){this.pluginRegistrar_=a;this.onAllFilesLoaded_=b;this.boundOnFileLoaded=jstestdriver.bind(this,this.onFileLoaded_);this.loadedFiles_=[]};jstestdriver.FileLoader.prototype.load=function(a){this.files_=a;if(this.files_.length>0){this.loadFile_(this.files_.shift())}else{this.onAllFilesLoaded_({loadedFiles:[]})}};jstestdriver.FileLoader.prototype.loadFile_=function(a){this.pluginRegistrar_.loadSource(a,this.boundOnFileLoaded)};jstestdriver.FileLoader.prototype.onFileLoaded_=function(a){this.loadedFiles_.push(a);if(this.files_.length==0){this.onAllFilesLoaded_({loadedFiles:this.loadedFiles_})}else{this.loadFile_(this.files_.shift())}};jstestdriver.TestCaseInfo=function(c,a,b){this.testCaseName_=c;this.template_=a;this.type_=b||jstestdriver.TestCaseInfo.DEFAULT_TYPE};jstestdriver.TestCaseInfo.DEFAULT_TYPE="default";jstestdriver.TestCaseInfo.prototype.getType=function(){return this.type_};jstestdriver.TestCaseInfo.prototype.getTestCaseName=function(){return this.testCaseName_};jstestdriver.TestCaseInfo.prototype.getTemplate=function(){return this.template_};jstestdriver.TestCaseInfo.prototype.getTestNames=function(){var b=[];for(var a in this.template_.prototype){if(a.indexOf("test")==0){b.push(a)}}return b};jstestdriver.TestCaseInfo.prototype.getDefaultTestRunConfiguration=function(){return this.createTestRunConfiguration_(this.getTestNames())};jstestdriver.TestCaseInfo.prototype.createTestRunConfiguration_=function(a){return new jstestdriver.TestRunConfiguration(this,a)};jstestdriver.TestCaseInfo.prototype.getTestRunConfigurationFor=function(e){var g={};var l=e.length;for(var c=0;c<l;c++){var j=e[c];var h=j.split(".");var a=g[h[0]];if(!a){a=[];g[h[0]]=a}if(h.length==2){a.push(h[1])}else{if(h.length==3&&h[1]=="prototype"){a.push(h[2])}else{}}}var d=g[this.testCaseName_];if(!d){return null}if(d.length==0){return this.createTestRunConfiguration_(this.getTestNames())}var b=[];var k=d.length;for(var c=0;c<k;c++){var f=d[c];if(f in this.template_.prototype){b.push(f)}}return this.createTestRunConfiguration_(b)};jstestdriver.TestCaseInfo.prototype.equals=function(a){return a&&typeof a.getTestCaseName!="undefined"&&a.getTestCaseName()==this.testCaseName_};jstestdriver.TestCaseInfo.prototype.toString=function(){return"TestCaseInfo("+this.testCaseName_+","+this.template_+","+this.type_+")"};jstestdriver.TestResult=function(g,b,a,d,c,f,e){this.testCaseName=g;this.testName=b;this.result=a;this.message=d;this.log=c;this.time=f;this.data=e||{}};jstestdriver.TestRunConfiguration=function(a,b){this.testCaseInfo_=a;this.tests_=b};jstestdriver.TestRunConfiguration.prototype.getTestCaseInfo=function(){return this.testCaseInfo_};jstestdriver.TestRunConfiguration.prototype.getTests=function(){return this.tests_};jstestdriver.TestCaseManager=function(a){this.testCasesInfo_=[];this.fileToTestCaseMap_={};this.latestTestCaseInfo_=null;this.pluginRegistrar_=a};jstestdriver.TestCaseManager.prototype.add=function(b){var a=this.indexOf_(b);if(a!=-1){this.testCasesInfo_.splice(a,1,b)}else{this.testCasesInfo_.push(b)}this.latestTestCaseInfo_=b};jstestdriver.TestCaseManager.prototype.testCaseAdded=function(){return this.latestTestCaseInfo_!=null};jstestdriver.TestCaseManager.prototype.updateLatestTestCase=function(a){if(this.latestTestCaseInfo_!=null){var c=this.indexOf_(this.latestTestCaseInfo_);if(c==this.testCasesInfo_.length-1){var b=this.fileToTestCaseMap_[a];if(b!=undefined&&b!=c){this.removeTestCase_(b)}}this.fileToTestCaseMap_[a]=c;this.latestTestCaseInfo_=null}};jstestdriver.TestCaseManager.prototype.removeTestCaseForFilename=function(a){var b=this.fileToTestCaseMap_[a];if(b!=undefined){this.removeTestCase_(b)}};jstestdriver.TestCaseManager.prototype.removeTestCase_=function(a){this.testCasesInfo_.splice(a,1)};jstestdriver.TestCaseManager.prototype.indexOf_=function(a){var c=this.testCasesInfo_.length;for(var b=0;b<c;b++){var d=this.testCasesInfo_[b];if(d.equals(a)){return b}}return -1};jstestdriver.TestCaseManager.prototype.getDefaultTestRunsConfiguration=function(){var a=[];var d=this.testCasesInfo_.length;for(var c=0;c<d;c++){var b=this.testCasesInfo_[c];a.push(b.getDefaultTestRunConfiguration())}return a};jstestdriver.TestCaseManager.prototype.getTestRunsConfigurationFor=function(b){var a=[];this.pluginRegistrar_.getTestRunsConfigurationFor(this.testCasesInfo_,b,a);return a};jstestdriver.TestCaseManager.prototype.getTestCasesInfo=function(){return this.testCasesInfo_};jstestdriver.TestCaseManager.prototype.getCurrentlyLoadedTest=function(){var h=[];var e=this.testCasesInfo_.length;for(var d=0;d<e;d++){var b=this.testCasesInfo_[d];var g=b.getTestCaseName();var f=b.getTestNames();var c=f.length;for(var a=0;a<c;a++){h.push(g+"."+f[a])}}return{numTests:h.length,testNames:h}};jstestdriver.TestCaseManager.prototype.getCurrentlyLoadedTestFor=function(g){var l=this.getTestRunsConfigurationFor(g);var m=l.length;var f=[];for(var e=0;e<m;e++){var k=l[e];var b=k.getTestCaseInfo().getTestCaseName();var a=k.getTests();var d=a.length;for(var c=0;c<d;c++){var h=a[c];f.push(b+"."+h)}}return{numTests:f.length,testNames:f}};jstestdriver.TestCaseBuilder=function(a){this.testCaseManager_=a};jstestdriver.TestCaseBuilder.prototype.TestCase=function(c,b){var a=function(){};if(b){a.prototype=b}if(typeof a.prototype.setUp=="undefined"){a.prototype.setUp=function(){}}if(typeof a.prototype.tearDown=="undefined"){a.prototype.tearDown=function(){}}this.testCaseManager_.add(new jstestdriver.TestCaseInfo(c,a));return a};jstestdriver.TestRunner=function(b,c){this.pluginRegistrar_=b;var a=this;this.boundOnTestRunConfigurationComplete=function(){c(jstestdriver.bind(a,a.onTestRunConfigurationComplete_))}};jstestdriver.TestRunner.prototype.runTests=function(a,c,d,b){this.testRunsConfiguration_=a;this.onTestDone_=c;this.onComplete_=d;this.captureConsole_=b;if(this.testRunsConfiguration_.length>0){this.runTestConfiguration_(this.testRunsConfiguration_.shift())}else{this.finish_()}};jstestdriver.TestRunner.prototype.runTestConfiguration_=function(a){if(this.captureConsole_){this.overrideConsole_()}this.pluginRegistrar_.runTestConfiguration(a,this.onTestDone_,this.boundOnTestRunConfigurationComplete);if(this.captureConsole_){this.resetConsole_()}};jstestdriver.TestRunner.prototype.finish_=function(){var a=this.onComplete_;this.testRunsConfiguration_=null;this.onTestDone_=null;this.onComplete_=null;this.captureConsole_=false;a()};jstestdriver.TestRunner.prototype.onTestRunConfigurationComplete_=function(){if(this.testRunsConfiguration_.length>0){this.runTestConfiguration_(this.testRunsConfiguration_.shift())}else{this.finish_()}};jstestdriver.TestRunner.prototype.overrideConsole_=function(){this.logMethod_=console.log;this.logDebug_=console.debug;this.logInfo_=console.info;this.logWarn_=console.warn;this.logError_=console.error;console.log=function(){jstestdriver.console.log.apply(jstestdriver.console,arguments)};console.debug=function(){jstestdriver.console.debug.apply(jstestdriver.console,arguments)};console.info=function(){jstestdriver.console.info.apply(jstestdriver.console,arguments)};console.warn=function(){jstestdriver.console.warn.apply(jstestdriver.console,arguments)};console.error=function(){jstestdriver.console.error.apply(jstestdriver.console,arguments)}};jstestdriver.TestRunner.prototype.resetConsole_=function(){console.log=this.logMethod_;console.debug=this.logDebug_;console.info=this.logInfo_;console.warn=this.logWarn_;console.error=this.logError_};jstestdriver.listen=function(){var g=jstestdriver.extractId(window.location.toString());var b=jstestdriver.SERVER_URL+g;jstestdriver.pluginRegistrar=new jstestdriver.PluginRegistrar();jstestdriver.testCaseManager=new jstestdriver.TestCaseManager(jstestdriver.pluginRegistrar);var d=new jstestdriver.TestRunner(jstestdriver.pluginRegistrar,function(h){h()});jstestdriver.testCaseBuilder=new jstestdriver.TestCaseBuilder(jstestdriver.testCaseManager);jstestdriver.global.TestCase=jstestdriver.bind(jstestdriver.testCaseBuilder,jstestdriver.testCaseBuilder.TestCase);var f=new jstestdriver.plugins.ScriptLoader(window,document,jstestdriver.testCaseManager);var e=new jstestdriver.plugins.StylesheetLoader(window,document,jstestdriver.jQuery.browser.mozilla||jstestdriver.jQuery.browser.safari);var c=new jstestdriver.plugins.FileLoaderPlugin(f,e);var a=new jstestdriver.plugins.TestRunnerPlugin(Date,function(){jstestdriver.jQuery("body").children().remove();jstestdriver.jQuery(document).unbind();jstestdriver.jQuery(document).die()});jstestdriver.pluginRegistrar.register(new jstestdriver.plugins.DefaultPlugin(c,a,new jstestdriver.plugins.AssertsPlugin(),new jstestdriver.plugins.TestCaseManagerPlugin()));jstestdriver.testCaseManager.TestCase=jstestdriver.global.TestCase;new jstestdriver.CommandExecutor(parseInt(g),b,jstestdriver.convertToJson(jstestdriver.jQuery.post),jstestdriver.testCaseManager,d,jstestdriver.pluginRegistrar).listen()};jstestdriver.TIMEOUT=500;jstestdriver.CommandExecutor=function(f,b,c,e,d,a){this.__id=f;this.__url=b;this.__sendRequest=c;this.__testCaseManager=e;this.__testRunner=d;this.__pluginRegistrar=a;this.__boundExecuteCommand=jstestdriver.bind(this,this.executeCommand);this.__boundExecute=jstestdriver.bind(this,this.execute);this.__boundEvaluateCommand=jstestdriver.bind(this,this.evaluateCommand);this.__boundSendData=jstestdriver.bind(this,this.sendData);this.boundCleanTestManager=jstestdriver.bind(this,this.cleanTestManager);this.boundOnFileLoaded_=jstestdriver.bind(this,this.onFileLoaded);this.boundOnFileLoadedRunnerMode_=jstestdriver.bind(this,this.onFileLoadedRunnerMode);this.commandMap_={execute:jstestdriver.bind(this,this.execute),runAllTests:jstestdriver.bind(this,this.runAllTests),runTests:jstestdriver.bind(this,this.runTests),loadTest:jstestdriver.bind(this,this.loadTest),reset:jstestdriver.bind(this,this.reset),registerCommand:jstestdriver.bind(this,this.registerCommand),dryRun:jstestdriver.bind(this,this.dryRun),dryRunFor:jstestdriver.bind(this,this.dryRunFor)};this.boundOnTestDone=jstestdriver.bind(this,this.onTestDone_);this.boundOnComplete=jstestdriver.bind(this,this.onComplete_);this.boundOnTestDoneRunnerMode=jstestdriver.bind(this,this.onTestDoneRunnerMode_);this.boundOnCompleteRunnerMode=jstestdriver.bind(this,this.onCompleteRunnerMode_);this.boundSendTestResults=jstestdriver.bind(this,this.sendTestResults_);this.boundOnDataSent=jstestdriver.bind(this,this.onDataSent_);this.testsDone_=[];this.sentOn_=-1;this.done_=false};jstestdriver.CommandExecutor.prototype.executeCommand=function(a){if(a=="noop"){this.__sendRequest(this.__url,null,this.__boundExecuteCommand)}else{var b=jsonParse(a);this.commandMap_[b.command](b.parameters)}};jstestdriver.CommandExecutor.prototype.sendData=function(a){this.__sendRequest(this.__url,a,this.__boundExecuteCommand)};jstestdriver.CommandExecutor.prototype.execute=function(b){var a={done:"",type:jstestdriver.RESPONSE_TYPES.COMMAND_RESULT,response:{response:this.__boundEvaluateCommand(b),browser:{"id":this.__id}}};this.sendData(a)};jstestdriver.CommandExecutor.prototype.findScriptTagsToRemove_=function(e,l){var c=e.getElementsByTagName("script");var a=l.length;var k=c.length;var b=[];for(var g=0;g<a;g++){var h=l[g].fileSrc;for(var d=0;d<k;d++){var m=c[d];if(m.src.indexOf(h)!=-1){b.push(m);break}}}return b};jstestdriver.CommandExecutor.prototype.removeScriptTags_=function(e,f){var d=e.getElementsByTagName("head")[0];var c=f.length;for(var b=0;b<c;b++){var a=f[b];d.removeChild(a)}};jstestdriver.CommandExecutor.prototype.removeScripts=function(b,a){this.removeScriptTags_(b,this.findScriptTagsToRemove_(b,a))};jstestdriver.CommandExecutor.prototype.loadTest=function(c){var e=c[0];var d=c[1]=="true"?true:false;var b=jsonParse('{"f":'+e+"}").f;this.removeScripts(document,b);var a=new jstestdriver.FileLoader(this.__pluginRegistrar,!d?this.boundOnFileLoaded_:this.boundOnFileLoadedRunnerMode_);a.load(b)};jstestdriver.CommandExecutor.prototype.getBrowserInfo=function(){return new jstestdriver.BrowserInfo(this.__id)};jstestdriver.CommandExecutor.prototype.onFileLoaded=function(a){var b=new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.FILE_LOAD_RESULT,JSON.stringify(a),this.getBrowserInfo());var c=new jstestdriver.CommandResponse(false,b);this.sendData(c)};jstestdriver.CommandExecutor.prototype.onFileLoadedRunnerMode=function(a){if(!parent.G_testRunner){parent.G_testRunner={finished_:false,success_:1,report_:"",filesLoaded_:0,isFinished:function(){return this.finished_},isSuccess:function(){return this.success_},getReport:function(){return this.report_},getNumFilesLoaded:function(){return this.filesLoaded_},setIsFinished:function(c){this.finished_=c},setIsSuccess:function(c){this.success_=c},setReport:function(c){this.report_=c},setNumFilesLoaded:function(c){this.filesLoaded_=c}}}var b=parent.G_testRunner;b.setNumFilesLoaded(a.loadedFiles.length);this.__sendRequest(this.__url,null,this.__boundExecuteCommand)};jstestdriver.CommandExecutor.prototype.runAllTests=function(b){var a=b[0];var c=b[1]=="true"?true:false;this.runTestCases_(this.__testCaseManager.getDefaultTestRunsConfiguration(),a=="true"?true:false,c)};jstestdriver.CommandExecutor.prototype.runTests=function(b){var c=jsonParse('{"expressions":'+b[0]+"}").expressions;var a=b[1];this.runTestCases_(this.__testCaseManager.getTestRunsConfigurationFor(c),a=="true"?true:false,false)};jstestdriver.CommandExecutor.prototype.runTestCases_=function(a,b,c){if(!c){this.startTestInterval_(jstestdriver.TIMEOUT);this.__testRunner.runTests(a,this.boundOnTestDone,this.boundOnComplete,b)}else{this.__testRunner.runTests(a,this.boundOnTestDoneRunnerMode,this.boundOnCompleteRunnerMode,b)}};jstestdriver.CommandExecutor.prototype.onTestDoneRunnerMode_=function(a){var b=parent.G_testRunner;b.setIsSuccess(b.isSuccess()&(a.result=="passed"));this.addTestResult(a)};jstestdriver.CommandExecutor.prototype.onCompleteRunnerMode_=function(){var a=parent.G_testRunner;a.setReport(JSON.stringify(this.testsDone_));this.testsDone_=[];a.setIsSuccess(a.isSuccess()==1?true:false);a.setIsFinished(true)};jstestdriver.CommandExecutor.prototype.startTestInterval_=function(a){this.timeout_=jstestdriver.setTimeout(this.boundSendTestResults,a)};jstestdriver.CommandExecutor.prototype.stopTestInterval_=function(){jstestdriver.clearTimeout(this.timeout_)};jstestdriver.CommandExecutor.prototype.onDataSent_=function(){if(this.done_){this.sendTestResultsOnComplete_()}else{if(this.sentOn_!=-1){var a=new Date().getTime()-this.sentOn_;this.sentOn_=-1;if(a<jstestdriver.TIMEOUT){this.startTestInterval_(jstestdriver.TIMEOUT-a)}else{this.sendTestResults_()}}}};jstestdriver.CommandExecutor.prototype.sendTestResults_=function(){this.stopTestInterval_();if(this.testsDone_.length>0){var a=new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.TEST_RESULT,JSON.stringify(this.testsDone_),this.getBrowserInfo());var b=new jstestdriver.CommandResponse(false,a);this.testsDone_=[];this.sentOn_=new Date().getTime();this.__sendRequest(this.__url,b,this.boundOnDataSent)}};jstestdriver.CommandExecutor.prototype.onTestDone_=function(a){this.addTestResult(a);if((a.result=="error"||a.log!="")&&this.sentOn_==-1){this.sendTestResults_()}};jstestdriver.CommandExecutor.prototype.addTestResult=function(a){this.__pluginRegistrar.processTestResult(a);this.testsDone_.push(a)};jstestdriver.CommandExecutor.prototype.sendTestResultsOnComplete_=function(){this.stopTestInterval_();this.done_=false;this.sentOn_=-1;var a=new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.TEST_RESULT,JSON.stringify(this.testsDone_),this.getBrowserInfo());var b=new jstestdriver.CommandResponse(true,a);this.testsDone_=[];this.__sendRequest(this.__url,b,this.__boundExecuteCommand)};jstestdriver.CommandExecutor.prototype.onComplete_=function(){this.done_=true;this.sendTestResultsOnComplete_()};jstestdriver.CommandExecutor.prototype.evaluateCommand=function(cmd){var res="";try{var evaluatedCmd=eval("("+cmd+")");if(evaluatedCmd){res=evaluatedCmd.toString()}}catch(e){res="Exception "+e.name+": "+e.message+"\n"+e.fileName+"("+e.lineNumber+"):\n"+e.stack}return res};jstestdriver.CommandExecutor.prototype.reset=function(){if(window.location.href.search("\\?refresh")!=-1){window.location.reload()}else{window.location.replace(window.location.href+"?refresh")}};jstestdriver.CommandExecutor.prototype.registerCommand=function(b,c){this[b]=jstestdriver.bind(this,c);var a=new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.REGISTER_RESULT,"Command "+b+" registered.",this.getBrowserInfo());this.sendData(new jstestdriver.CommandResponse("",a))};jstestdriver.CommandExecutor.prototype.dryRun=function(){var a=new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.TEST_QUERY_RESULT,JSON.stringify(this.__testCaseManager.getCurrentlyLoadedTest()),this.getBrowserInfo());this.sendData(new jstestdriver.CommandResponse(true,a))};jstestdriver.CommandExecutor.prototype.dryRunFor=function(a){var c=jsonParse('{"expressions":'+a[0]+"}").expressions;var b=JSON.stringify(this.__testCaseManager.getCurrentlyLoadedTestFor(c));var d=new jstestdriver.CommandResponse(true,new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.TEST_QUERY_RESULT,b,this.getBrowserInfo()));this.sendData(d)};jstestdriver.CommandExecutor.prototype.listen=function(){if(window.location.href.search("\\?refresh")!=-1){var a=new jstestdriver.CommandResponse(true,new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.RESET_RESULT,"Runner reset.",this.getBrowserInfo()));this.__sendRequest(this.__url+"?start",a,this.__boundExecuteCommand)}else{this.__sendRequest(this.__url+"?start",null,this.__boundExecuteCommand)}};/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
jstestdriver.plugins.ScriptLoader = function(win, dom, testCaseManager) {
  this.win_ = win;
  this.dom_ = dom;
  this.testCaseManager_ = testCaseManager;
  this.originalDocumentWrite_ = this.dom_.write;
};


jstestdriver.plugins.ScriptLoader.prototype.load = function(file, callback) {
  this.fileResult_ = null;
  var head = this.dom_.getElementsByTagName('head')[0];
  var script = this.dom_.createElement('script');
  var handleError = jstestdriver.bind(this, function(msg, url, line) {
    var loadMsg = 'error loading file: ' + file.fileSrc;

    if (line != undefined && line != null) {
      loadMsg += ':' + line;
    }
    if (msg != undefined && msg != null) {
      loadMsg += ': ' + msg;
    }
    this.updateResult_(new jstestdriver.FileResult(file, false, loadMsg));
  });

  this.win_.onerror = handleError; 
  script.onerror = handleError;
  if (!jstestdriver.jQuery.browser.opera) {
    script.onload = jstestdriver.bind(this, function() {
      this.onLoad_(file, callback);
    });
  }
  script.onreadystatechange = jstestdriver.bind(this, function() {
    if (script.readyState == 'loaded') {
      this.onLoad_(file, callback);
    }
  });
  script.type = "text/javascript";
  script.src = file.fileSrc;
  this.disableDocumentWrite();
  head.appendChild(script);
};


jstestdriver.plugins.ScriptLoader.prototype.onLoad_ = function(file, callback) {
  if (this.testCaseManager_.testCaseAdded()) {
    this.testCaseManager_.updateLatestTestCase(file.fileSrc);
  }
  this.updateResult_(new jstestdriver.FileResult(file, true, ''));
  this.win_.onerror = jstestdriver.EMPTY_FUNC;
  this.restoreDocumentWrite();
  callback(this.fileResult_);
};


jstestdriver.plugins.ScriptLoader.prototype.updateResult_ = function(fileResult) {
  if (this.fileResult_ == null) {
    this.fileResult_ = fileResult;
  } else {
    this.testCaseManager_.removeTestCaseForFilename(fileResult.file.fileSrc);
  }
};


jstestdriver.plugins.ScriptLoader.prototype.disableDocumentWrite = function() {
  this.dom_.write = jstestdriver.EMPTY_FUNC;
};


jstestdriver.plugins.ScriptLoader.prototype.restoreDocumentWrite = function() {
  this.dom_.write = this.originalDocumentWrite_;
};
/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
jstestdriver.plugins.StylesheetLoader = function(win, dom, synchronousCallback) {
  this.win_ = win;
  this.dom_ = dom;
  this.synchronousCallback_ = synchronousCallback;
};


jstestdriver.plugins.StylesheetLoader.prototype.load = function(file, callback) {
  this.fileResult_ = null;
  var head = this.dom_.getElementsByTagName('head')[0];
  var link = this.dom_.createElement('link');
  var handleError = jstestdriver.bind(this, function(msg, url, line) {
    var loadMsg = 'error loading file: ' + file.fileSrc;

    if (line != undefined && line != null) {
      loadMsg += ':' + line;
    }
    if (msg != undefined && msg != null) {
      loadMsg += ': ' + msg;
    }
    this.updateResult_(new jstestdriver.FileResult(file, false, loadMsg));
  });

  this.win_.onerror = handleError;
  link.onerror = handleError;
  if (!jstestdriver.jQuery.browser.opera) {
    link.onload = jstestdriver.bind(this, function() {
      this.onLoad_(file, callback);
    });
  }
  link.onreadystatechange = jstestdriver.bind(this, function() {
    if (link.readyState == 'loaded') {
      this.onLoad_(file, callback);
    }
  });
  link.type = "text/css";
  link.rel = "stylesheet";
  link.href = file.fileSrc;
  head.appendChild(link);

  // Firefox and Safari don't seem to support onload or onreadystatechange for link
  if (this.synchronousCallback_) {
    this.onLoad_(file, callback);
  }
};


jstestdriver.plugins.StylesheetLoader.prototype.onLoad_ = function(file, callback) {
  this.updateResult_(new jstestdriver.FileResult(file, true, ''));
  this.win_.onerror = jstestdriver.EMPTY_FUNC;
  callback(this.fileResult_);  
};


jstestdriver.plugins.StylesheetLoader.prototype.updateResult_ = function(fileResult) {
  if (this.fileResult_ == null) {
    this.fileResult_ = fileResult;
  }
};
/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
jstestdriver.plugins.FileLoaderPlugin = function(scriptLoader, stylesheetLoader) {
  this.scriptLoader_ = scriptLoader;
  this.stylesheetLoader_ = stylesheetLoader;
};


jstestdriver.plugins.FileLoaderPlugin.prototype.loadSource = function(file, onSourceLoaded) {
  if (file.fileSrc.match(/\.css$/)) {
    this.stylesheetLoader_.load(file, onSourceLoaded);
  } else {
    this.scriptLoader_.load(file, onSourceLoaded);
  }
};
/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
jstestdriver.plugins.TestRunnerPlugin = function(dateObj, clearBody) {
  this.dateObj_ = dateObj;
  this.clearBody_ = clearBody;
};


jstestdriver.plugins.TestRunnerPlugin.prototype.runTestConfiguration = function(
    testRunConfiguration, onTestDone, onTestRunConfigurationComplete) {
  var testCaseInfo = testRunConfiguration.getTestCaseInfo();
  var tests = testRunConfiguration.getTests();
  var size = tests.length;

  for (var i = 0; i < size; i++) {
    var testName = tests[i];

    onTestDone(this.runTest(testCaseInfo.getTestCaseName(), testCaseInfo.getTemplate(), testName));
  }
  onTestRunConfigurationComplete();
};


jstestdriver.plugins.TestRunnerPlugin.prototype.runTest = function(testCaseName, testCase,
    testName) {
  var testCaseInstance;

  try {
    testCaseInstance = new testCase();
  } catch (e) {
    return new jstestdriver.TestResult(testCaseName, testName, 'error',
        testCaseName + ' is not a test case', '', 0);
  }
  var start = new this.dateObj_().getTime();

  jstestdriver.expectedAssertCount = -1;
  jstestdriver.assertCount = 0;
  var res = 'passed';
  var msg = '';

  try {
    if (testCaseInstance.setUp) {
      testCaseInstance.setUp();
    }
    if (!(testName in testCaseInstance)) {
      var err = new Error(testName + ' not found in ' + testCaseName);

      err.name = 'AssertError';
      throw err;
    }
    testCaseInstance[testName]();
    if (jstestdriver.expectedAssertCount != -1 &&
        jstestdriver.expectedAssertCount != jstestdriver.assertCount) {
      var err = new Error("Expected '" +
          jstestdriver.expectedAssertCount +
          "' asserts but '" +
          jstestdriver.assertCount +
          "' encountered.");

      err.name = 'AssertError';
      throw err;
    }
  } catch (e) {

    // We use the global here because of a circular dependency. The isFailure plugin should be
    // refactored.
    res = jstestdriver.pluginRegistrar.isFailure(e) ? 'failed' : 'error';
    msg = JSON.stringify(e);
  }
  try {
    if (testCaseInstance.tearDown) {
      testCaseInstance.tearDown();
    }
  } catch (e) {
    if (res == 'passed' && msg == '') {
      res = 'error';
      msg = JSON.stringify(e);
    }
  }
  this.clearBody_();
  var end = new this.dateObj_().getTime();

  return new jstestdriver.TestResult(testCaseName, testName, res, msg,
      jstestdriver.console.getLog(), end - start);  
};
/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
jstestdriver.plugins.DefaultPlugin = function(fileLoaderPlugin,
                                              testRunnerPlugin,
                                              assertsPlugin,
                                              testCaseManagerPlugin) {
  this.fileLoaderPlugin_ = fileLoaderPlugin;
  this.testRunnerPlugin_ = testRunnerPlugin;
  this.assertsPlugin_ = assertsPlugin;
  this.testCaseManagerPlugin_ = testCaseManagerPlugin;
};


jstestdriver.plugins.DefaultPlugin.name = 'defaultPlugin';


jstestdriver.plugins.DefaultPlugin.prototype.loadSource = function(file, onSourceLoaded) {
  return this.fileLoaderPlugin_.loadSource(file, onSourceLoaded);
};


jstestdriver.plugins.DefaultPlugin.prototype.runTestConfiguration = function(testRunConfiguration,
    onTestDone, onTestRunConfigurationComplete) {
  return this.testRunnerPlugin_.runTestConfiguration(testRunConfiguration, onTestDone,
      onTestRunConfigurationComplete);
};


jstestdriver.plugins.DefaultPlugin.prototype.isFailure = function(exception) {
  return this.assertsPlugin_.isFailure(exception);
};


jstestdriver.plugins.DefaultPlugin.prototype.getTestRunsConfigurationFor =
    function(testCaseInfos, expressions, testRunsConfiguration) {
  return this.testCaseManagerPlugin_.getTestRunsConfigurationFor(testCaseInfos,
                                                                expressions,
                                                                testRunsConfiguration);
};
/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
jstestdriver.plugins.AssertsPlugin = function() {
};


jstestdriver.plugins.AssertsPlugin.prototype.isFailure = function(e) {
  return e.name == 'AssertError';
};
/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


/**
 * Plugin that handles the default behavior for the TestCaseManager.
 * @author corysmith@google.com (Cory Smith)
 */
jstestdriver.plugins.TestCaseManagerPlugin = function() {};


/**
 * Write testRunconfigurations retrieved from testCaseInfos defined by expressions.
 * @param {Array.<jstestdriver.TestCaseInfo>} testCaseInfos The loaded test case infos.
 * @param {Array.<String>} The expressions that define the TestRunConfigurations
 * @parma {Array.<jstestdriver.TestRunConfiguration>} The resultant array of configurations.
 */
jstestdriver.plugins.TestCaseManagerPlugin.prototype.getTestRunsConfigurationFor =
    function(testCaseInfos, expressions, testRunsConfiguration) {
  var size = testCaseInfos.length;
  for (var i = 0; i < size; i++) {
    var testCaseInfo = testCaseInfos[i];
    var testRunConfiguration = testCaseInfo.getTestRunConfigurationFor(expressions);

    if (testRunConfiguration != null) {
      testRunsConfiguration.push(testRunConfiguration);
    }
  }
  return true;
};
