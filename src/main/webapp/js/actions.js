function Actions() {

  this.SCANADD = function () {
    window.open("http://192.168.32.74/Scanning/Scanning.aspx", "_blank");
  };

  this.EDIT = function () {
    var currentItems = _getCurrentItems();
    // TODO: что если currentItems > 1
    if (currentItems.length > 1) return;
    // TODO: сделать доступным получение свойств для папки
    var isDoc = currentItems.attr('mime') ? true : false;
    if (!isDoc) return;

    $.ajax({
      method: "GET",
      url: properties.host + "/webapi/documents/" + currentItems.attr('data-tt-id') + "/properties"
    }).done(done.bind(this, {
      func: function (data, params) {
        var form = _createForm(data);
        _showDialog(form, 'Редактирование документа', {
          "Сохранить": function () {
            var form = $(this).find('form');
            form.validate();
            if (!form.valid()) return;
            var data = $(form).serializeArray();
            _fixBooleanValues($(form), data);
            _addDataToSerializedArray(form, data, ['type']);
            _updateDocument({dialog: this, data: data, id: params.id});
          },
          "Отмена": function () {
            _closeDialog(this);
          }
        });
      }, params: {id: currentItems.attr("data-tt-id")}
    })).fail(fail);

  };

  this.PROP = function () {
    var currentItems = _getCurrentItems();
    // TODO: что если currentItems > 1
    if (currentItems.length > 1) return;
    // TODO: сделать доступным получение свойств для папки
    var isDoc = currentItems.attr('mime') ? true : false;
    if (!isDoc) return;

    $.ajax({
      method: "GET",
      url: properties.host + "/webapi/documents/" + currentItems.attr('data-tt-id') + "/properties"
    }).done(done.bind(this, {
      func: function (data) {
        var form = _createForm(_getReadonlyFields(data));
        _showDialog(form, 'Просмотр свойств', {
          "ОК": function () {
            _closeDialog(this);
          }
        });
      }
    })).fail(fail);
  };

  this.ADD = function () {
    var currentItems = _getCurrentItems();
    var isDoc = currentItems.attr('mime') ? true : false;
    var folderPath = getAbsoluteFolderPath(currentItems);

    $.ajax({
      url: properties.host + "/webapi/classes?folderId=" + folderPath,
      method: "GET",
      beforeSend: function (request) {
        request.setRequestHeader("ECM-Ticket", ticket);
      }
    }).done(done.bind(this, {
      func: function (data, params) {
        var classField = [
          {
            "name": "className",
            "value": "",
            "type": "SELECT",
            "defaultValue": null,
            "readOnly": false,
            "hidden": false,
            "displayName": "Класс документа",
            "required": true,
            "description": "Класс документа"
          }
        ];
        for (var i = 0; i < data.length; i++) {
          if (!classField[0].defaultValue) {
            classField[0].defaultValue = [
              {
                name: '',
                displayName: ''
              }
            ];
          }
          classField[0].defaultValue.push({name: data[i].name, displayName: data[i].displayName});
        }


        var form = _createForm(classField, [], [
          {'name': 'enctype', 'value': 'multipart/form-data'},
          {'name': 'data-folderPath', 'value': params.folderPath}
        ]);

        $(form).find('#_className').change(function () {
          _cleanAdditionalFields(form, "afterClassChanged");


          if (this.value == '')return;


          $.ajax({
            url: properties.host + "/webapi/classes/" + this.value + "/properties",
            method: "GET",
            contentType: "application/json; charset=utf-8"

          }).done(done.bind(this, {
            func: _addFieldsToForm, params: {
              form: form,
              fieldsId: "afterClassChanged"
            }
          })).fail(fail);


        });

        _showDialog(form, 'Добавление документа', {
          "Сохранить": function () {
            var form = $(this).find('form');
            form.validate();
            if (!form.valid()) return;
            var data = $(form).serializeArray();
            _fixBooleanValues($(form), data);
            _addDataToSerializedArray(form, data, ['type']);

            if (!_setBase64Content($(form), data, _saveDocument, {form: form, dialog: this, data: data})) {
              _closeDialog(this);
            }
          },
          "Отмена": function () {
            _closeDialog(this);
          }
        });


      }, params: {folderPath: folderPath}
    })).fail(fail);


  };

  this.DELETE = function () {
    var currentItems = _getCurrentItems();
    // TODO: что если currentItems > 1
    if (currentItems.length > 1) return;
    // TODO: сделать доступным получение свойств для папки
    var isDoc = currentItems.attr('mime') ? true : false;
    if (!isDoc) return;

    if (confirm('Удалить ' + currentItems.text() + ' ?')) {
      $.ajax({
        method: "DELETE",
        url: properties.host + "/webapi/documents/" + currentItems.attr('data-tt-id')
      }).done(done.bind(this, {
        func: function (data) {
          buildItemsTree();
          alert("Документ успешно удалён");
        }
      })).fail(fail);
    }

  };

  this.REPORTS = function () {
    var $formContainer = $('#' + DIALOG_FORM);
    var $from = _createForm(
        [
          {
            displayName: 'Результат проверки',
            type: 'SELECT',
            name: 'checkResult',
            defaultValue:
              [
                {name: 'Хорошо', displayName: 'Хорошо'},
                {name: 'Удовлетворительно', displayName: 'Удовлетворительно'},
                {name: 'Плохо', displayName: 'Плохо'}
              ]
          },
          {displayName: 'Комментарий', value: 'Коммент', type: 'STRING', name: 'comment'}
        ],
        [],
        [
          {'name': 'data-folderPath', 'value': getAbsoluteFolderPath(_getCurrentItems())}
        ]
    );
    $formContainer.append($from);
    $formContainer.dialog({
      title: 'Документ по шаблону',
      width: '30%',
      modal: true,
      buttons: {
        "OK": function() {
          var form = $(this).find('form');
          var data = _jsonFromForm(form);
          data.folderPath = form.attr('data-folderPath');
          var message = JSON.stringify(data);
          console.log(message);
          $.ajax({
            method: "POST",
            url: properties.host + "/webapi/templateReport",
            data: message,
            contentType: "application/json"
          }).done(done.bind(this, {func:
              function(data) {
                doneF.initTreetable(data);
                buildItemsTree();
              }})).fail(fail);

          _closeDialog(this);
        }
      }
    });
  };

  function _jsonFromForm(form) {
    var data = $(form).serializeArray();
    var values = {};
    $.each(data, function() {
      values[this.name] = this.value;
    });
    return values;
  }

  function _addDataToSerializedArray(form, data, attrName) {

    for (var i = 0; i < data.length; i++) {
      var item = $(form).find("#_" + data[i].name)[0];
      if (!item) continue;
      for (var j = 0; j < attrName.length; j++) {
        data[i][attrName[j]] = $(item).attr('data-' + attrName[j]);
      }
    }

  }

  function _cleanAdditionalFields(form, id) {
    $(form).find('#' + id).empty();
  }


  function _addFieldsToForm(data, params) {
    params.form.find('.properties-table').append(_createTbody(data, _getBase64ContentField(), {id: params.fieldsId}));
  }

  function _showDialog(form, title, buttons) {
    var formContainer = $('#' + DIALOG_FORM);
    formContainer.append(form);
    var dialog = formContainer.dialog({
      title: title,
      autoOpen: false,
      width: '50%',
      modal: true,
      buttons: buttons,
      close: function () {
        form[0].reset();
      }
    });
    dialog.dialog("open");
  }

  function _getReadonlyFields(fields) {
    var output = JSON.parse(JSON.stringify(fields));
    for (var i = 0; i < output.length; i++) {
      output[i].readOnly = true;
    }
    return output;
  }

  function _getCurrentItems() {
    return $('.treetable tr.selected');
  }

  function _closeDialog(dialog) {
    $(dialog).dialog("close");
  }

  function _saveDocument(params) {
    var dataToSend = _getDataToSend(params.data);
    dataToSend.folderPath = params.form.attr('data-folderPath');
    console.info(dataToSend);

    $.ajax({
      method: "POST",
      url: properties.host + "/webapi/documents",
      data: JSON.stringify(dataToSend),
      contentType: "application/json; charset=utf-8"
    }).done(done.bind(this, {
      func: function (data) {
        buildItemsTree();
        alert("Документ успешно создан");
      }
    })).fail(fail);

    _closeDialog(params.dialog);
  }

  function _updateDocument(params) {
    var dataToSend = _getDataToSend(params.data);
    console.info(dataToSend);

    $.ajax({
      method: "PUT",
      url: properties.host + "/webapi/documents/" + params.id,
      data: JSON.stringify(dataToSend),
      contentType: "application/json; charset=utf-8"
    }).done(done.bind(this, {
      func: function (data) {
        buildItemsTree();
        alert("Документ успешно обновлён");
      }
    })).fail(fail);

    _closeDialog(params.dialog);
  }

  function _getDataToSend(data) {
    var out = {
      folderPath: null,
      document: {
        properties: [],
        className: null,
        content: null
      }
    };
    for (var i = 0; i < data.length; i++) {
      switch (data[i].name) {
        case 'base64Content':
          out.document.content = {
            base64Content: data[i].value,
            mimeType: data[i].mimeType,
            contentName: data[i].contentName
          };
          break;
        case 'className':
          out.document.className = data[i].value;
          break;
        default :
          out.document.properties.push({
            name: data[i].name,
            value: data[i].value.toString(),
            type: data[i].type
          });
          break;
      }
    }
    return out;
  }

  function _setBase64Content(form, data, callback, callbackParams) {
    var input = form.find('input[type=file]');
    if (!input[0]) return false;
    var file = input[0].files[0];
    if (!file) return false;
    var reader = new FileReader();
    reader.onload = function (file, readerEvt) {
      var binaryString = readerEvt.target.result;
      data.push({
        'name': 'base64Content',
        'value': /*'data:' + file.type + ';base64,' +*/ btoa(binaryString),
        'contentName': file.name,
        'mimeType': file.type
      });
      callback(callbackParams);
    }.bind(this, file);
    reader.readAsBinaryString(file);
  }

  function _getBase64ContentField() {
    var tr = $('<tr>'), leftTd = $('<td>'), rightTd = $('<td>');
    var label = $('<label>', {'for': '_base64Content', 'text': 'Документ'});
    var input = $('<input>', {'type': 'file', 'name': 'base64Content', 'id': '_base64Content', 'required': 'required'});
    $(leftTd).append(label);
    $(rightTd).append(input);
    tr.append(leftTd).append(rightTd);
    return tr;
  }

  function _fixBooleanValues(form, data) {
    var booleans = form.find('input[type=checkbox]');

    for (var i = 0; i < data.length; i++) {
      for (var j = 0; j < booleans.length; j++) {
        if (data[i].name == booleans[j].name) {
          data[i].value = $(booleans[j]).prop("checked");
          booleans.splice(j, 1);
        }
      }
    }

    for (var i = 0; i < booleans.length; i++) {
      data.push({
        'name': booleans.attr('name'),
        'value': $(booleans[i]).prop("checked")
      });
    }

  }

  function _createForm(fields, additionalFields, formAttributes) {
    console.log(fields);
    console.log(additionalFields);
    console.log(formAttributes);
    var form = $('<form>', {'class': 'properties-form', 'method': 'post'}),
      table = $('<table>', {'class': 'properties-table'});

    if (formAttributes) {
      for (var i = 0; i < formAttributes.length; i++) {
        form.attr(formAttributes[i].name, formAttributes[i].value);
      }
    }

    $(table).append(_createTbody(fields, additionalFields));


    $(form).append(table);
    return $(form);
  }

  function _createTbody(fields, additionalFields, params) {
    var tbody = $('<tbody>', params);
    for (var i = 0; i < fields.length; i++) {
      var formField,
        fieldLabel,
        tr = $('<tr>'), leftTd = $('<td>'), rightTd = $('<td>');
      switch (fields[i].type.toLowerCase()) {
        case 'boolean':
          formField = $('<input>', {'type': 'checkbox', 'disabled': fields[i].readOnly});
          break;
        case 'select':
          formField = $('<select>', {'disabled': fields[i].readOnly});
          break;
        default:
          formField = $('<input>', {'type': 'text'});
          break;
      }

      $(formField).attr({
        'id': '_' + fields[i].name,
        'name': fields[i].name,
        'title': fields[i].description,
        'required': fields[i].required,
        'readonly': fields[i].readOnly,
        'data-type': fields[i].type
      });

      fieldLabel = $('<label>', {'text': fields[i].displayName, 'for': '_' + fields[i].name});

      if (fields[i].hidden == true) {
        $(formField).addClass('hidden');
      }
      if (fields[i].defaultValue != null) {
        if (fields[i].type.toLowerCase() == 'select') {
          $(fields[i].defaultValue).each(function (key, value) {
            $(formField).append($('<option>').attr('value', value.name).text(value.displayName));
          });
        } else {
          $(formField).attr('value', fields[i].defaultValue);
        }
      }
      if (fields[i].value != null) {
        $(formField).attr('value', fields[i].value);
      }

      $(leftTd).append(fieldLabel);
      $(rightTd).append(formField);

      $(tr).append(leftTd).append(rightTd);
      $(tbody).append(tr);
    }
    if (additionalFields) {
      for (var i = 0; i < additionalFields.length; i++) {
        tbody.append(additionalFields[i]);
      }
    }
    return tbody;
  }

}