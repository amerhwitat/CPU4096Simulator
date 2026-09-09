export class MemoryFault extends Error { constructor(message, code='MEMORY_FAULT', address=0n){super(message);this.code=code;this.address=address;} }
export class MemoryBus {
  constructor(size=16*1024*1024){this.size=size;this.data=new Uint8Array(size);this.mmio=new Map();this.regions=[];this.wxorx=true;this.readOnly=[];this.execOnly=[];}
  map(start,end,device){this.mmio.set(`${start}:${end}`,device);this.regions.push({start,end,device});}
  region(addr){return this.regions.find(r=>addr>=r.start&&addr<r.end);}
  check(addr,len=1,write=false,exec=false){if(addr<0||addr+len>this.size)throw new MemoryFault('address out of range','PAGE_FAULT',BigInt(addr));if(exec&&this.execOnly.some(r=>addr>=r[0]&&addr+len<=r[1]))return;if(write&&this.wxorx&&this.readOnly.some(r=>addr>=r[0]&&addr+len<=r[1]))throw new MemoryFault('write to read-only region','PROTECTION_FAULT',BigInt(addr));}
  read(addr,len){const r=this.region(addr);if(r?.device?.read)return r.device.read(addr-r.start,len);this.check(addr,len);return this.data.slice(addr,addr+len)}
  write(addr,bytes){const b=bytes instanceof Uint8Array?bytes:Uint8Array.from(bytes);const r=this.region(addr);if(r?.device?.write)return r.device.write(addr-r.start,b);this.check(addr,b.length,true);this.data.set(b,addr)}
  readU64(addr){let x=0n;for(const b of this.read(addr,8))x=(x<<8n)|BigInt(b);return x}
  writeU64(addr,v){let x=BigInt(v);const b=new Uint8Array(8);for(let i=7;i>=0;i--){b[i]=Number(x&255n);x>>=8n}this.write(addr,b)}
  protectReadOnly(start,end){this.readOnly.push([start,end])}
  protectExecutable(start,end){this.execOnly.push([start,end])}
  snapshot(){return{size:this.size,mmio:this.regions.map(r=>({start:r.start,end:r.end,device:r.device?.name??'device'})),readOnly:this.readOnly,execOnly:this.execOnly}}
}
export class VirtualMemory { constructor(){this.pages=new Map();this.pageSize=4096;this.next=0x100000;} alloc(bytes,perm='rw'){const n=Math.ceil(bytes/this.pageSize)*this.pageSize,a=this.next;this.next+=n;this.pages.set(a,{size:n,perm});return a;} free(addr){return this.pages.delete(addr)} translate(addr,access='r'){for(const [base,p] of this.pages){if(addr>=base&&addr<base+p.size){if(!p.perm.includes(access))throw new MemoryFault('virtual permission fault','PROTECTION_FAULT',BigInt(addr));return addr-base}}throw new MemoryFault('unmapped virtual address','PAGE_FAULT',BigInt(addr))}}
