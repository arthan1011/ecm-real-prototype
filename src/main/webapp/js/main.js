var properties = getProperties();
var doneF = new DoneFunctions();
var ticket;
var permissionCache = new Cache();

$(document).ready(function () {
  getTicket();
});

var ACTION_NAME = 'data-action',
  DIALOG_FORM = 'dialog-form';

function buildInterface() {
  buildActionMenuBar();
  buildItemsTree();
}

function getTicket() {
  $.ajax({
    url: properties.host + "/webapi/acquireTicket",
    method: "POST",
    data: JSON.stringify({
      "rootFolderPath": properties.rootFolderPath,
      "extSystemId": "extSystenUd",
      "username": "user",
      "accessTemplateName": properties.accessTemplateName
    }),
    contentType: "application/json; charset=utf-8"

  }).done(done.bind(this, {func: doneF.getTicket})).fail(fail);
}

function buildItemsGrid() {
  $.ajax({
    url: properties.host + "/webapi/ui/gridcolumns",
    method: "GET",
    beforeSend: function (request) {
      request.setRequestHeader("ECM-Ticket", ticket);
    }
  }).done(done.bind(this, {func: doneF.buildItemGrid})).fail(fail);
}

function fillItemsGrid() {
  $.ajax({
    url: properties.host + "/webapi/ui/hierarchy",
    method: "GET",
    beforeSend: function (request) {
      request.setRequestHeader("ECM-Ticket", ticket);
    },
    contentType: "application/json; charset=utf-8"
  }).done(done.bind(this, {func: doneF.fillItemsGrid})).fail(fail);
}

function buildActionMenuBar() {
  clear('actionMenuBar');
  $.ajax({
    url: properties.host + "/webapi/ui/buttons",
    method: "GET",
    beforeSend: function (request) {
      request.setRequestHeader("ECM-Ticket", ticket);
    },
    contentType: "application/json; charset=utf-8"
  }).done(done.bind(this, {func: doneF.buildActionMenuBar})).fail(fail);
}

function actionClick() {
  var a = new Actions();
  clear(DIALOG_FORM);
  a[this.getAttribute(ACTION_NAME)]();
}

function buildItemsTree() {
  clear('tree');
  buildItemsGrid();
}

function getPropertyByName(item, name) {
  for (var i = 0; i < item.properties.length; i++) {
    if (item.properties[i].name && item.properties[i].name.toLowerCase() == name) return item.properties[i];
  }
  return {_empty: true};
}

function _fillTable(grid, item, parent) {
  var tr = $('<tr>', {
    'data-tt-id': getPropertyByName(item, 'id').value,
    'mime': getPropertyByName(item, 'mime').value
  });
  parent ? $(tr).attr('data-tt-parent-id', getPropertyByName(parent, 'id').value) : null;

  $(grid).find('th').each(function (pos, th) {
    var propertyId = $(th).attr('column_id');
    var property;
    if (propertyId == "DocumentTitle") {
      property = getPropertyByName(item, propertyId);
      if (property._empty) {
        property = getPropertyByName(item, "name");
        propertyId = "name";
      }
    } else {
      property = getPropertyByName(item, propertyId);
    }
    var html;

    if (property && property.type && property.type.toLowerCase() == "boolean") {
      Boolean.valueOf();
      html = $('<input>', {type: 'checkbox', checked: eval(property.value), disabled: property.readOnly});
    } else {
      html = $('<div>', {property_id: propertyId});
      html.html(getPropertyByName(item, propertyId).value);
    }

    var td = $('<td>');

    td.append(html);

    $(tr).append(td);
  });

  $(grid).append(tr);

  if (item.subfolders && item.subfolders.length > 0) {
    for (var i = 0; i < item.subfolders.length; i++) {
      _fillTable(grid, item.subfolders[i], item);
    }
  }

  if (item.documents && item.documents.length > 0) {
    for (var i = 0; i < item.documents.length; i++) {
      _fillTable(grid, item.documents[i], item);
    }
  }

}

function clear(id) {
  var container = $('#' + id);
  if (!container) return;
  $(container).empty();
}

function changeActionBarByPermissions(id, path) {
  if (!id)console.error(id, path);
  if (permissionCache.get(id)) {
    updateActionBar(permissionCache.get(id));
    return;
  }
  permissionCache.add(id);

  $.ajax({
    method: "GET",
    url: properties.host + "/webapi/permissions?folderPath=" + path,
    beforeSend: function (request) {
      request.setRequestHeader("ECM-Ticket", ticket);
    }
  }).done(done.bind(this, {
    func: doneF.changeActionBarByPermissions, params: {
      id: id,
      permissionCache: permissionCache,
      callback: updateActionBar
    }
  })).fail(fail);

}

function updateActionBar(actions) {
  var actionBar = $('#actionMenuBar');
  actionBar.find('button').each(function (pos, el) {
    $(el).attr('disabled', 'disabled');
  });
  for (var i = 0; i < actions.length; i++) {

    for (var j = 0; j < mapPermissionToButtons[actions[i]].length; j++) {
      var b = actionBar.find('.' + mapPermissionToButtons[actions[i]][j])[0];
      if (b) b.removeAttribute('disabled');
    }

  }
}

function initTreetable() {
  var tree = $("#tree");
  tree.find("> table").treetable({expandable: true});
  tree.find("> table tr").dblclick(function (event) {
    var tr = event.currentTarget;
    var id = tr.getAttribute('data-tt-id');
    var isDoc = tr.getAttribute('mime');
    if (!$(this).hasClass('selected'))$(this).addClass('selected');
    if (isDoc == null) {
      if ($(tr).hasClass('collapsed')) {
        $("#tree").find("> table").treetable('expandNode', id);
      } else {
        $("#tree").find("> table").treetable('collapseNode', id);
      }
      return;
    }
    console.info("Был дабл клик по документу " + id);

    $.ajax({
      url: properties.host + "/webapi/documents/" + id + "/content",
      method: "GET"
    }).done(done.bind(this, {func: doneF.initTreetable})).fail(fail);
  });

  // Highlight selected row
  tree.find("> table tbody").on("mousedown", "tr", function () {
    $(".selected").not(this).removeClass("selected");
    if (!$(this).hasClass('selected')) {
      $(this).toggleClass("selected");
    }

    var id = $(this).attr('data-tt-id');
    var parentId = $(this).attr('data-tt-parent-id');
    var path = getRelativeFolderPath($(this));
    var isDoc = $(this).attr('mime');
    if (!isDoc) {
      changeActionBarByPermissions(id, path);
    } else {
      var temp = path.split('/');
      temp.pop();
      changeActionBarByPermissions(parentId, temp.join('/'));
    }
  });

  tree.find("> table .folder").each(function () {
    $(this).parents("#tree > table tr").droppable({
      accept: ".file, .folder",
      drop: function (e, ui) {
        var droppedEl = ui.draggable.parents("tr");
        $("#tree").find("> table").treetable("move", droppedEl.data("ttId"), $(this).data("ttId"));
      },
      hoverClass: "accept",
      over: function (e, ui) {
        var droppedEl = ui.draggable.parents("tr");
        if (this != droppedEl[0] && !$(this).is(".expanded")) {
          $("#tree").find("> table").treetable("expandNode", $(this).data("ttId"));
        }
      }
    });
  });
}

function getAbsoluteFolderPath(item) {
  var relativePath = getRelativeFolderPath(item);
  var out = relativePath.split('/');
  if (out.length <= 2) return properties.rootFolderPath;
  out.shift();
  out.shift();
  return properties.rootFolderPath + '/' + out.join('/');
}

function getRelativeDocumentPath(item) {
  if (!item.attr('mime')) return false;
  return _getItemPath(item);
}

function getRelativeFolderPath(item) {
  if (item.attr('mime')) return _getItemPath(item, '');
  return _getItemPath(item, '/' + item.text().trim());
}

function _getItemPath(item, text) {
  var parentId = item.attr('data-tt-parent-id');

  var parentElement = $('#tree').find('[data-tt-id="' + parentId + '"]');

  while (parentElement.length > 0) {
    text = '/' + parentElement.text().trim() + text;
    parentElement = $('#tree').find('[data-tt-id="' + parentElement.attr('data-tt-parent-id') + '"]');
  }
  return text;
}