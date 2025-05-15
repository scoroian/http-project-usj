function updateRouteContainer() {
    const method = document.getElementById('method').value;
    const routeContainer = document.getElementById('routeContainer');
    let html = '';
  
    if (method === 'GET') {
      html += `<label for="routeSelect">Ruta del recurso:</label>
               <select id="routeSelect" class="form-select">
                 <option value="/">/</option>
                 <option value="/items">/items</option>
               </select>`;
    } else if (method === 'POST') {
      html += `<label for="routeInput">Ruta del recurso:</label>
               <input type="text" id="routeInput" class="form-control" value="/items" readonly>`;
    } else if (method === 'PUT' || method === 'DELETE') {
      html += `<label>Ruta del recurso:</label>
               <div class="input-group">
                 <input type="text" id="baseRoute" class="form-control" value="/items/" readonly>
                 <input type="text" id="idField" class="form-control" placeholder="ID del item">
               </div>`;
    } else {
      html += `<label for="routeInput">Ruta del recurso:</label>
               <input type="text" id="routeInput" class="form-control" value="" readonly>`;
    }
    routeContainer.innerHTML = html;
  }
  
  document.getElementById('method').addEventListener('change', updateRouteContainer);
  window.addEventListener('load', updateRouteContainer);
  
  async function sendRequest() {
    const method = document.getElementById('method').value;
    let path = '';

    if (method === 'GET') {
      path = document.getElementById('routeSelect').value;
    } else if (method === 'POST') {
      path = document.getElementById('routeInput').value;
    } else if (method === 'PUT' || method === 'DELETE') {
      const baseRoute = document.getElementById('baseRoute').value;
      const idField = document.getElementById('idField').value;
      path = baseRoute + idField;
    } else {
      path = '';
    }
  
    const body = document.getElementById('body').value;
  
    let options = {
      method,
      headers: {}
    };
  
    if (method === 'POST' || method === 'PUT') {
      options.headers['Content-Type'] = 'text/plain';
      options.body = body;
    }
  
    try {
      const response = await fetch(`http://localhost:8080${path}`, options);
      const text = await response.text();
      document.getElementById('response').textContent =
        `Estado: ${response.status} ${response.statusText}\n\n${text}`;
    } catch (error) {
      document.getElementById('response').textContent = 'Error: ' + error.message;
    }
  }
  