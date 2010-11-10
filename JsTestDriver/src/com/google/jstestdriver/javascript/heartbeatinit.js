jstestdriver.createHeartbeat = function(capturePath) {
  function getBody() {
    return document.getElementsByTagName('body')[0];
  }

  function createElement(elementName) {
    return document.createElement(elementName);
  }

  var view = new jstestdriver.HeartbeatView(getBody, createElement);

  function sendRequest(url, data, success, error) {
    jstestdriver.jQuery.ajax({
      type : 'POST',
      url : url,
      data : data,
      success : success,
      error : error,
      dataType : 'text'
    });
  }

  function getTime() {
    return new Date().getTime();
  }

  function redirectToPath(path){
    var location = top.location;
    top.location = jstestdriver.createPath(location.toString(), path);
  }

  var id = jstestdriver.extractId(window.location.toString());
  var heartbeatPath = jstestdriver.createPath(window.location.toString(),
      jstestdriver.HEARTBEAT_URL);
  return new jstestdriver.Heartbeat(id,
                                    heartbeatPath,
                                    capturePath,
                                    sendRequest,
                                    2000,
                                    jstestdriver.setTimeout,
                                    getTime,
                                    view,
                                    redirectToPath);
};
