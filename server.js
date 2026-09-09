import http from 'node:http';
import { readFile } from 'node:fs/promises';
import { extname, normalize } from 'node:path';
import { fileURLToPath } from 'node:url';
import { CpuCore, RuntimeCatalog, isaCatalog, WIDTHS } from './src/chimera.js';
import { generateEd25519, hash, hmac, randomHex } from './src/keygen.js';
import { LearningKernel128D, TrustStore, DESKTOP_PROFILES, BootDesktopProgress, ConcurrencyModel } from './src/koronos.js';
import { MemoryBus, VirtualMemory } from './src/memory.js';
import { Scheduler, IpcBus, SyscallTable, ServiceManager } from './src/kernel.js';
import { PacketBus, UdpStack, TcpStateMachine, DnsTable, NetlinkModel } from './src/network.js';
import { BrainNetwork, State128D, DeterministicTrace } from './src/brain.js';
import { RoboticsHAL, SwarmScheduler } from './src/robotics.js';
import { assembleLine, isaJson } from './src/assembler.js';
import { ChimeraIR, compileIR } from './src/toolchain.js';
const publicDir=new URL('./public/',import.meta.url);const mime={'.html':'text/html; charset=utf-8','.js':'text/javascript; charset=utf-8','.css':'text/css; charset=utf-8','.json':'application/json; charset=utf-8'};
export function createServer(opts={}){const runtime=new RuntimeCatalog(),learning=new LearningKernel128D(),trust=new TrustStore(),progress=new BootDesktopProgress(),concurrency=new ConcurrencyModel();const memory=new MemoryBus();const vm=new VirtualMemory();const scheduler=new Scheduler(4),ipc=new IpcBus(),syscalls=new SyscallTable(),services=new ServiceManager();const packets=new PacketBus(),udp=new UdpStack(packets),tcp=new TcpStateMachine(),dns=new DnsTable(),netlink=new NetlinkModel();const brain=new BrainNetwork(),trace=new DeterministicTrace(),robotics=new RoboticsHAL(),swarm=new SwarmScheduler();netlink.register('route',['GET','SET']);netlink.register('netdev',['GET','SET']);
const server=http.createServer(async(req,res)=>{try{
 if(req.url==='/api/health')return json(res,{ok:true,name:'Chimera II Web Runtime',node:process.version,features:['cpu','isa','memory','kernel','network','toolchain','brain','robotics','keygen']});
 if(req.url==='/api/isa')return json(res,{widths:WIDTHS,defined:21,total:284,instructions:isaCatalog});
 if(req.url==='/api/isa.json')return text(res,isaJson(),'application/json; charset=utf-8');
 if(req.url==='/api/runtime/catalog')return json(res,runtime.json());
 if(req.url==='/api/cpu/state')return json(res,new CpuCore(8192).snapshot());
 if(req.url==='/api/kernel/state')return json(res,{scheduler:scheduler.snapshot(),ipc:ipc.snapshot(),syscalls:[...syscalls.handlers?.keys?.()??[]],services:services.snapshot()});
 if(req.url==='/api/memory/state')return json(res,{physical:memory.snapshot(),virtual:{pages:vm.pages.size,next:vm.next}});
 if(req.url==='/api/network/state')return json(res,{packets:packets.snapshot(),tcp:tcp.snapshot(),dns:dns.records?Object.fromEntries(dns.records):{},netlink:netlink.snapshot(),udpSockets:[...udp.sockets.keys()]});
 if(req.url==='/api/brain/state')return json(res,brain.trace());
 if(req.url==='/api/robotics/state')return json(res,{hal:robotics.snapshot(),swarm:swarm.snapshot()});
 if(req.url==='/api/koronos/state')return json(res,{learning:learning.snapshot(),trust:trust.list(),desktopProfiles:DESKTOP_PROFILES,progress:progress.state(),concurrency:concurrency.state()});
 if(req.url==='/api/keygen')return json(res,generateEd25519());
 if(req.url==='/api/random')return json(res,{bytes:randomHex(32)});
 if(req.url==='/api/hash'){const body=await readBody(req);return json(res,{algorithm:body.algorithm||'sha256',hash:hash(body.data||'',body.algorithm||'sha256')})}
 if(req.url==='/api/hmac'){const body=await readBody(req);return json(res,{algorithm:body.algorithm||'sha256',hmac:hmac(body.data||'',body.key||'',body.algorithm||'sha256')})}
 if(req.url==='/api/assemble'){const body=await readBody(req);return json(res,{instruction:assembleLine(body.source||'')})}
 if(req.url==='/api/compile'){const body=await readBody(req);const ir=new ChimeraIR();for(const op of body.ops||[])ir.emit(op.op,op.args||[]);return json(res,{ir:ir.toJSON(),assembly:compileIR(ir)})}
 if(req.url==='/api/sim/step'){const body=await readBody(req);const c=new CpuCore(Number(body.bits||8192));if(body.a)c.regs[1]=c.regs[1].constructor.fromBigInt(BigInt(body.a),c.bits);if(body.b)c.regs[2]=c.regs[2].constructor.fromBigInt(BigInt(body.b),c.bits);const i={opcode:Number(body.opcode||1),dst:0,srcA:1,srcB:2,immediate:BigInt(body.immediate||0)};const out=c.execute(i);return json(res,{result:out?.hex()??null,state:c.snapshot()})}
 if(req.url.startsWith('/api/'))return json(res,{error:'not found'},404);
 let p=req.url==='/'?'index.html':req.url.split('?')[0];p=normalize(p).replace(/^([.][.][/\\])+/, '');const file=new URL(p.slice(0,1)==='/'?'.'+p:p,publicDir);const data=await readFile(file);res.writeHead(200,{'Content-Type':mime[extname(p)]||'application/octet-stream','Cache-Control':'no-cache'});res.end(data);
 }catch(e){json(res,{error:e.message,code:e.code??'RUNTIME_ERROR'},500)}});if(opts.listen)server.listen(opts.port??Number(process.env.PORT||3000));return server}
function json(res,obj,status=200){res.writeHead(status,{'Content-Type':'application/json; charset=utf-8','Access-Control-Allow-Origin':'*'});res.end(JSON.stringify(obj))}function text(res,s,type){res.writeHead(200,{'Content-Type':type,'Access-Control-Allow-Origin':'*'});res.end(s)}
function readBody(req){return new Promise((resolve,reject)=>{let s='';req.on('data',d=>{s+=d;if(s.length>1e6)req.destroy()});req.on('end',()=>{try{resolve(JSON.parse(s||'{}'))}catch(e){reject(e)}});req.on('error',reject)})}
if(process.argv[1]===fileURLToPath(import.meta.url))createServer({port:Number(process.env.PORT||3000),listen:true}).on('listening',()=>console.log(`Chimera II Web Runtime listening on http://localhost:${process.env.PORT||3000}`));
