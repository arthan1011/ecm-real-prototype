function Cache() {
  var cache = {}; // id: [locked: true\false, value: []]

  this.add = function (id, value) {
    var cacheById = this.get(id);
    if (!cacheById) {
      cache[id] = value;
    }
  };

  this.edit = function (id, value) {
    cache[id] = value;
  };

  this.get = function (id) {
    return cache[id];
  };

  this.delete = function (id) {
    delete cache[id];
  };

  this.showAll = function () {
    console.info(cache);
  }

}