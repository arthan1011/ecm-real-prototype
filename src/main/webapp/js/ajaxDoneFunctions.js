function DoneFunctions() {
  this.getTicket = function (data) {
    ticket = data.ticket;
    buildInterface();
  };

  this.buildItemGrid = function (data) {
    var columns = data;

    var grid = $('<table>', {width: '100%'});
    var tr = $('<tr>', {'class': 'table_head'});
    for (var i = 0; i < columns.length; i++) {
      $(tr).append($('<th>', {
        text: columns[i].labelText,
        width: columns[i].widthPct,
        column_id: columns[i].propertyName
      }));
    }
    $(grid).append(tr);
    $('#tree').append(grid);


    fillItemsGrid();
  };

  this.fillItemsGrid = function (data) {
    var grid = $('#tree').find('> table')[0];
    _fillTable(grid, data);
    initTreetable();
  };

  this.initTreetable = function (data) {
    var preview = $("#preview");
    $(preview).empty();
    var embed = $('<embed>', {width: '100%', height: '100%', src: "data:application/pdf;base64," + data});
    $(preview).append(embed);
  };

  this.buildActionMenuBar = function (data) {
    console.log(data);
    var actions = data;
    var actionMenuBar = $('#actionMenuBar');
    for (var i = 0; i < actions.length; i++) {
      var button = $('<button/>', {
        'type': 'button',
        //'disabled': 'disabled',
        'class': actions[i],
        'data-action': actions[i],
        'title': actions[i]
      }).bind('click', actionClick);
      $(actionMenuBar).append(button);
    }
  };

  this.changeActionBarByPermissions = function (data, params) {
    params.permissionCache.edit(params.id, data);
    params.callback(data);
  }
}