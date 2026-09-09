import { assembleLine, disassembleInstruction } from './assembler.js';
export class ChimeraIR { constructor(){this.ops=[]} emit(op,args=[]){this.ops.push({op,args});return this} toJSON(){return{version:1,ops:this.ops}} }
export function compileIR(ir){return ir.ops.map(x=>`${x.op} ${x.args.join(', ')}`).join('\n')+'\n'}
export function link(images,entry=0){const bytes=Uint8Array.from(images.flatMap(x=>Array.from(x)));return{entry,size:bytes.length,bytes,format:'CHMOBJ1'}}
export function inspectObject(obj){return{format:obj.format,entry:obj.entry,size:obj.bytes.length,shaPreview:Array.from(obj.bytes.slice(0,16)).map(x=>x.toString(16).padStart(2,'0')).join('')}}
export function disassemble(bytes,cpu){const out=[];for(let p=0;p+15<bytes.length;p+=16){const i=cpu.decode(bytes.slice(p,p+16));out.push(`${p.toString(16).padStart(8,'0')}: ${disassembleInstruction(i)}`)}return out.join('\n')}
export { assembleLine };
