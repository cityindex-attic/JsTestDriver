
var jstd = jstestdriver;

jstd.CssController = function(head, dom, styleSheetList, location) {
  this.head_ = head;
  this.dom_ = dom;
  this.styleSheetList_ = styleSheetList;
  this.location_ = location;
};


jstd.CssController.prototype.load = function(path, callback) {
  var link = this.dom_.createElement('link');
  link.type = "text/css";
  link.rel = "stylesheet";
  link.href = path;
  this.head_.append(link);

  var href = this.location_ + link.href;
  var self = this;
  this.setTimeout(function() {
    for (var i = 0; self.styleSheetList_[i]; i++) {
      if (self.styleSheetList_[i].href == href) {
        callback(path);
        return;
      }
    }
  }, 100);
};


jstd.CssController.prototype.reducePath_ = function(uri) {
  var parts = uri.split('/');
  var stack = [];
  for (var i = 0; parts[i]; i++) {
    if (parts[i] == '..') {
      stack.pop();
    } else if (parts[i] != '.') {
      stack.push(parts[i]);
    }
  }
  return stack.join(''/');
};
