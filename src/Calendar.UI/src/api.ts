async function callApi() {
  let resp = await fetch('http://localhost:5003/events/all');
  if (resp.ok) {
    return resp.json();
  }
}

async function searchApi(start: Date, end: Date) {
  let resp = await fetch(`http://localhost:5003/events?start=${start.toISOString()}&end=${end.toISOString()}`);
  if (resp.ok) {
    return resp.json();
  }
}

async function callPastApi() {
  let resp = await fetch('http://localhost:5003/events/past');
  if (resp.ok) {
    return resp.json();
  }
}

export {
  callApi,
  searchApi,
  callPastApi
}