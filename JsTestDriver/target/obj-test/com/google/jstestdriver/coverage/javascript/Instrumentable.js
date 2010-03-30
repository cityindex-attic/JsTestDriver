/**
 *
 * This file depends on JQuery
 *
 * Note: The vast majority of method names and AJAX calls are tokensed
 **/

/***********************************************************************
 * This Section FIXES a sync problem with ASP callbacks
 **********************************************************************/
function WebForm_CallbackComplete_SyncFixed() {
     for (var i = 0; i < __pendingCallbacks.length; i++) {
        callbackObject = __pendingCallbacks[ i ];
        if (callbackObject && callbackObject.xmlRequest && (callbackObject.xmlRequest.readyState == 4)) {
            if (!__pendingCallbacks[ i ].async) {
                __synchronousCallBackIndex = -1;
            }
            __pendingCallbacks[i] = null;
            var callbackFrameID = "__CALLBACKFRAME" + i;
            var xmlRequestFrame = document.getElementById(callbackFrameID);
            
            if (xmlRequestFrame) {
                xmlRequestFrame.parentNode.removeChild(xmlRequestFrame);
            }
            executeCallback(callbackObject);
        }
    }
}

$(document).ready(function() {
    if (typeof (callbackComplete) == "function") {
      // set the original version with fixed version
      callbackComplete = callbackComplete_SyncFixed;
    }
});

 var ID = "idTreeView";
 
 /**
 * Checks to see if the tree view actually exists on the page
 **/
function doesTreeViewExist()
{

	if($('#' + ID).length == 0)
	{
		return false;
	}
	else
	{
	return true;
	}
}
