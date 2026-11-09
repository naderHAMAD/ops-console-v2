const http = require('http');
const jobs = {};
let nextId = 100;

const server = http.createServer((req, res) => {
  console.log(req.method, req.url); // ← Logging en première ligne
  
  const url = req.url;

  if (req.method === 'POST' && url.match(/\/api\/v2\/job_templates\/\d+\/launch\//)) {
    const id = nextId++;
    jobs[id] = { id, status: 'running' };
    setTimeout(() => { jobs[id].status = 'successful'; }, 8000);
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ id, status: 'pending' }));
    return;
  }

  const jobMatch = url.match(/\/api\/v2\/jobs\/(\d+)\/stdout\//);
  if (jobMatch) {
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end('PLAY [Simulation] ***\nTASK [OK] ***\nok: [target]\nPLAY RECAP\ntarget: ok=1 changed=1 failed=0\n');
    return;
  }

  const statusMatch = url.match(/\/api\/v2\/jobs\/(\d+)\//);
  if (statusMatch) {
    const id = statusMatch[1];
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(jobs[id] || { id, status: 'successful' }));
    return;
  }

  res.writeHead(404);
  res.end();
});

server.listen(8052, () => console.log('Faux AWX actif sur http://localhost:8052'));
