import http from 'node:http';
import { readFile } from 'node:fs/promises';
import { extname, normalize } from 'node:path';
import { fileURLToPath } from 'node:url';
import { CpuCore, RuntimeCatalog, isaCatalog, WIDTHS } from './src/chimera.js';
import { generateEd25519, hash, hmac, randomHex } from './src/keygen.js';
import { LearningKernel128D, TrustStore, DESKTOP_PROFILES, BootDesktopProgress, ConcurrencyModel } from './src/koronos.js';
const publicDir=new URL('./public/',import.meta.url);const mime={'.html':'text/html; charset=utf-8','.js':'text/javascript; charset=utf-8','.css':'text/css; charset=utf-8','.json':'application/json; charset=utf-8'};
export function createServer(opts={}){const runtime=new RuntimeCatalog(),learning=new LearningKernel128D(),trust=new TrustStore(),progress=new BootDesktopProgress(),concurrency=new ConcurrencyModel();const server=http.createServer(async(req,res)=>{try{
 if(req.url==='/api/health')return json(res,{ok:true,name:'Chimera II Web Runtime',node:process.version});
 if(req.url==='/api/isa')return json(res,{widths:WIDTHS,defined:21,total:284,instructions:isaCatalog});
 if(req.url==='/api/runtime/catalog')return json(res,runtime.json());
 if(req.url==='/api/cpu/state')return json(res,new CpuCore(8192).snapshot());
 if(req.url==='/api/koronos/state')return json(res,{learning:learning.snapshot(),trust:trust.list(),desktopProfiles:DESKTOP_PROFILES,progress:progress.state(),concurrency:concurrency.state()});
 if(req.url==='/api/keygen')return json(res,generateEd25519());
 if(req.url==='/api/random')return json(res,{bytes:randomHex(32)});
 if(req.url==='/api/hash'){const body=await readBody(req);return json(res,{algorithm:body.algorithm||'sha256',hash:hash(body.data||'',body.algorithm||'sha256')})}
 if(req.url==='/api/hmac'){const body=await readBody(req);return json(res,{algorithm:body.algorithm||'sha256',hmac:hmac(body.data||'',body.key||'',body.algorithm||'sha256')})}
 if(req.url.startsWith('/api/'))return json(res,{error:'not found'},404);
 let p=req.url==='/'?'index.html':req.url.split('?')[0];p=normalize(p).replace(/^([.][.][/\\])+/, '');const file=new URL(p.slice(0,1)==='/'?'.'+p:p,publicDir);const data=await readFile(file);res.writeHead(200,{'Content-Type':mime[extname(p)]||'application/octet-stream','Cache-Control':'no-cache'});res.end(data);
 }catch(e){json(res,{error:e.message},500)}});if(opts.listen)server.listen(opts.port??Number(process.env.PORT||3000));return server}
function json(res,obj,status=200){res.writeHead(status,{'Content-Type':'application/json; charset=utf-8','Access-Control-Allow-Origin':'*'});res.end(JSON.stringify(obj))}
function readBody(req){return new Promise((resolve,reject)=>{let s='';req.on('data',d=>{s+=d;if(s.length>1e6)req.destroy()});req.on('end',()=>{try{resolve(JSON.parse(s||'{}'))}catch(e){reject(e)}});req.on('error',reject)})}
if(process.argv[1]===fileURLToPath(import.meta.url))createServer({port:Number(process.env.PORT||3000),listen:true}).on('listening',()=>console.log(`Chimera II Web Runtime listening on http://localhost:${process.env.PORT||3000}`));
