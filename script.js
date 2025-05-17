function updateRouteContainer() {
    const method = document.getElementById('method').value;
    const routeContainer = document.getElementById('routeContainer');
    let html = '';

    if (method === 'GET') {
      html += `
        <label>Ruta del recurso:</label>
        <div class="form-check">
          <input class="form-check-input" type="radio" name="getOption" id="getRoot" value="root" checked>
          <label class="form-check-label" for="getRoot">/</label>
        </div>
        <div class="form-check">
          <input class="form-check-input" type="radio" name="getOption" id="getItems" value="items">
          <label class="form-check-label" for="getItems">/items</label>
        </div>
        <div class="form-check">
          <input class="form-check-input" type="radio" name="getOption" id="getById" value="byId">
          <label class="form-check-label" for="getById">/items/{id}</label>
        </div>
        <div id="getIdContainer" class="mt-2" style="display:none;">
          <input type="text" id="getIdField" class="form-control" placeholder="ID del item">
        </div>
      `;
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

    // Mostrar/ocultar campo de ID en GET
    if (method === 'GET') {
      document.querySelectorAll('input[name="getOption"]').forEach(radio => {
        radio.addEventListener('change', () => {
          const container = document.getElementById('getIdContainer');
          container.style.display = document.getElementById('getById').checked ? 'block' : 'none';
        });
      });
    }
  }
  
  document.getElementById('method').addEventListener('change', updateRouteContainer);
  window.addEventListener('load', updateRouteContainer);
  
  async function sendRequest() {
    const method = document.getElementById('method').value;
    let path = '';
  
    if (method === 'GET') {
      const option = document.querySelector('input[name="getOption"]:checked').value;
      if (option === 'root') {
        path = '/';
      } else if (option === 'items') {
        path = '/items';
      } else {
        const id = document.getElementById('getIdField').value;
        path = '/items/' + id;
      }
    } else if (method === 'POST') {
      path = document.getElementById('routeInput').value;
    } else if (method === 'PUT' || method === 'DELETE') {
      const baseRoute = document.getElementById('baseRoute').value;
      const id = document.getElementById('idField').value;
      path = baseRoute + id;
    }
  
    const body = document.getElementById('body').value;
  
    const options = { method, headers: {} };
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