import test from 'node:test';
import assert from 'node:assert/strict';
import { CpuCore } from '../src/chimera.js';
import { MemoryBus, VirtualMemory } from '../src/memory.js';
import { Scheduler, IpcBus, CapabilitySet } from '../src/kernel.js';
import { PacketBus, DnsTable, TcpStateMachine } from '../src/network.js';
import { State128D, BrainNetwork } from '../src/brain.js';
import { RoboticsHAL, SwarmScheduler } from '../src/robotics.js';
import { assembleLine } from '../src/assembler.js';

test('memory and virtual memory',()=>{const m=new MemoryBus(1024);m.write(4,Uint8Array.from([1,2,3]));assert.deepEqual([...m.read(4,3)],[1,2,3]);const v=new VirtualMemory();const a=v.alloc(8192,'rwx');assert.equal(v.translate(a+7,'x'),7)});
test('kernel scheduler IPC capabilities',()=>{const s=new Scheduler(2);const p=s.spawn('init',10);p.capabilities.grant('net');assert.equal(p.capabilities.has('net'),true);assert.equal(s.tick().length,1);const i=new IpcBus();i.send('x',{v:1});assert.deepEqual(i.recv('x'),{v:1})});
test('network models',()=>{const b=new PacketBus();b.send({protocol:'UDP',payload:new Uint8Array([1])});assert.equal(b.recv().protocol,'UDP');const d=new DnsTable();d.put('host','::1','AAAA');assert.equal(d.resolve('host','AAAA'),'::1');const t=new TcpStateMachine();t.connect();assert.equal(t.state,'ESTABLISHED')});
test('128D brain and robotics',()=>{const s=new State128D();assert.equal(s.values.length,128);const n=new BrainNetwork();n.addNode('a',s);n.addNode('b',s);n.connect('a','b',1);assert.equal(n.step().nodes.length,2);const h=new RoboticsHAL();h.registerSensor('x',()=>42);assert.equal(h.read('x'),42);const swarm=new SwarmScheduler();swarm.add('a',0);swarm.add('b',1);assert.equal(swarm.balance().length,2)});
test('assembler accepts canonical instruction',()=>{assert.equal(assembleLine('ADD R1, R2, R3').opcode,1);const c=new CpuCore(4096);c.regs[2]=c.regs[2].constructor.fromBigInt(4n,4096);c.regs[3]=c.regs[3].constructor.fromBigInt(5n,4096);c.execute(assembleLine('ADD R1, R2, R3'));assert.equal(c.regs[1].toBigInt(),9n)});
