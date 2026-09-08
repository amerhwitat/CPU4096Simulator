export const WIDTHS = Object.freeze({ P4096: 4096, CHIMERA_II: 8192 });
export const OPCODES = Object.freeze({
  ADD:0x0001,SUB:0x0002,AND:0x0003,OR:0x0004,XOR:0x0005,NOT:0x0006,MOV:0x0007,
  SHL:0x0008,SHR:0x0009,MUL:0x000a,MULHI:0x000b,ROL:0x000c,ROR:0x000d,DIV:0x000e,
  REM:0x000f,CMP:0x0010,CMPEQ:0x0011,CMPLT:0x0012,SYS_CALL:0x0013,BARRIER:0x0014,POLICY_UPDATE:0x0015
});
const MASK_CACHE = new Map();
const mask = bits => { if (!MASK_CACHE.has(bits)) MASK_CACHE.set(bits,(1n<<BigInt(bits))-1n); return MASK_CACHE.get(bits); };
const asBig = v => typeof v === 'bigint' ? v : BigInt(v);
export class WideWord {
  constructor(value=0n,bits=4096){this.bits=bits;this.value=asBig(value)&mask(bits)}
  static fromBigInt(v,bits=4096){return new WideWord(v,bits)}
  static zero(bits){return new WideWord(0n,bits)}
  toBigInt(){return this.value}
  hex(){return this.value.toString(16).padStart(this.bits/4,'0')}
  add(o){return new WideWord(this.value+o.value,this.bits)} sub(o){return new WideWord(this.value-o.value,this.bits)}
  and(o){return new WideWord(this.value&o.value,this.bits)} or(o){return new WideWord(this.value|o.value,this.bits)} xor(o){return new WideWord(this.value^o.value,this.bits)}
  not(){return new WideWord(~this.value,this.bits)} shl(n){return new WideWord(this.value<<BigInt(n),this.bits)} shr(n){return new WideWord(this.value>>BigInt(n),this.bits)}
  rol(n){const s=BigInt(n)%BigInt(this.bits);return new WideWord((this.value<<s)|(this.value>>(BigInt(this.bits)-s)),this.bits)}
  ror(n){const s=BigInt(n)%BigInt(this.bits);return new WideWord((this.value>>s)|(this.value<<(BigInt(this.bits)-s)),this.bits)}
  mul(o){return new WideWord(this.value*o.value,this.bits)} mulhi(o){return new WideWord((this.value*o.value)>>BigInt(this.bits),this.bits)}
  div(o){if(o.value===0n)throw new RangeError('division by zero');return new WideWord(this.value/o.value,this.bits)}
  rem(o){if(o.value===0n)throw new RangeError('division by zero');return new WideWord(this.value%o.value,this.bits)}
}
export class CpuCore {
  constructor(bits=8192,registerCount=1024){this.bits=bits;this.regs=Array.from({length:registerCount},()=>WideWord.zero(bits));this.pc=0n;this.privilege=0;this.flags={z:false,n:false,c:false,v:false}}
  execute(i){
    const a=this.regs[i.srcA]??WideWord.zero(this.bits),b=this.regs[i.srcB]??WideWord.zero(this.bits);let out;
    switch(i.opcode){
      case OPCODES.ADD:out=a.add(b);break;case OPCODES.SUB:out=a.sub(b);break;case OPCODES.AND:out=a.and(b);break;case OPCODES.OR:out=a.or(b);break;case OPCODES.XOR:out=a.xor(b);break;case OPCODES.NOT:out=a.not();break;case OPCODES.MOV:out=a;break;
      case OPCODES.SHL:out=a.shl(Number(i.immediate??0n));break;case OPCODES.SHR:out=a.shr(Number(i.immediate??0n));break;case OPCODES.MUL:out=a.mul(b);break;case OPCODES.MULHI:out=a.mulhi(b);break;
      case OPCODES.ROL:out=a.rol(Number(i.immediate??0n));break;case OPCODES.ROR:out=a.ror(Number(i.immediate??0n));break;case OPCODES.DIV:out=a.div(b);break;case OPCODES.REM:out=a.rem(b);break;
      case OPCODES.CMP:this.flags={z:a.value===b.value,n:a.value<b.value,c:a.value>=b.value,v:false};break;case OPCODES.CMPEQ:out=WideWord.fromBigInt(a.value===b.value?1n:0n,this.bits);break;case OPCODES.CMPLT:out=WideWord.fromBigInt(a.value<b.value?1n:0n,this.bits);break;
      case OPCODES.SYS_CALL:if(this.privilege<1)throw new Error('SYS_CALL requires privilege >= 1');break;case OPCODES.BARRIER:break;case OPCODES.POLICY_UPDATE:if(this.privilege<3)throw new Error('POLICY_UPDATE requires privilege >= 3');break;
      default:throw new Error(`unsupported opcode 0x${Number(i.opcode).toString(16).padStart(4,'0')}`)
    }
    if(out)this.regs[i.dst]=out;this.pc+=16n;return out??null
  }
  encode(i){const b=new Uint8Array(16),dv=new DataView(b.buffer);dv.setUint16(0,i.opcode,true);dv.setUint16(2,i.dst,true);dv.setUint16(4,i.srcA,true);dv.setUint16(6,i.srcB,true);let x=asBig(i.immediate??0n);for(let k=0;k<8;k++){b[8+k]=Number(x&255n);x>>=8n}return b}
  decode(bytes){const b=bytes instanceof Uint8Array?bytes:new Uint8Array(bytes),dv=new DataView(b.buffer,b.byteOffset,b.byteLength);let x=0n;for(let k=7;k>=0;k--)x=(x<<8n)|BigInt(b[8+k]);return{opcode:dv.getUint16(0,true),dst:dv.getUint16(2,true),srcA:dv.getUint16(4,true),srcB:dv.getUint16(6,true),immediate:x}}
  snapshot(){return{bits:this.bits,pc:this.pc.toString(),privilege:this.privilege,registers:this.regs.slice(0,32).map(r=>r.hex())}}
}
export class RuntimeCatalog {
  constructor(){this.commands=['bash','zsh','fish','pwsh','coreutils','uutils','awk','gawk','sed','grep','find','xargs','sort','uniq','cut','tr','diff','patch','tar','cpio','gzip','bzip2','xz','zstd','jq','yq','make','gcc','g++','binutils','gdb','python','java','dotnet','node','npm','perl','ruby','php','curl','wget','rsync','ssh','scp','sftp','git','openssl','screen','tmux','nano','vim','emacs','less','ip','ss','ping','traceroute','dig','nslookup'];this.services=['sshd','samba','nfs','bind9','dnsmasq','apache','nginx','caddy','postfix','dovecot','chrony','rsyslog','cron','systemd','dbus','avahi','cups','bluez','libvirtd','podman','docker','containerd','k3s','postgresql','mariadb','redis','mosquitto'];this.filesystems=['FAT','exFAT','NTFS','ReFS','ext4','XFS','Btrfs','ZFS','NFS4','SMB3','UFS','APFS','HFS+','tmpfs','overlayfs'];this.network=['IPv4','IPv6','TCP','UDP','DNS','SMB','NFS','netlink','WireGuard','OpenVPN'];this.platforms=['Linux','Windows','Windows Server','macOS','FreeBSD','WSL','WSL2','WSLg','x86-64','ARM64']}
  json(){return{commands:this.commands,services:this.services,filesystems:this.filesystems,network:this.network,platforms:this.platforms}}
}
export const isaCatalog=Array.from({length:284},(_,n)=>({opcode:n+1,hex:`0x${(n+1).toString(16).padStart(4,'0')}`,defined:n<21,name:Object.entries(OPCODES).find(([,v])=>v===n+1)?.[0]??`OP_${(n+1).toString(16).padStart(4,'0')}`}));
