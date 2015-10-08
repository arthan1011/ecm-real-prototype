function done(callback, responce) {
  if (!responce) fail();
  if (responce.result != null) {
    callback.func(responce.result, callback.params);
  } else {
    fail(responce.error);
  }
  console.info(responce);
}

function fail(error) {
  if (error.error) {
    console.error(error.error);
  } else {
    console.error(error);
  }
}

function always(data) {

}