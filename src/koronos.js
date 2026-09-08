export class Vector128D {
  constructor(values=[]){this.values=Float64Array.from({length:128},(_,i)=>Number(values[i]??0));}
  add(o){return new Vector128D(this.values.map((v,i)=>v+o.values[i]))}
  scale(s){return new Vector128D(this.values.map(v=>v*s))}
  dot(o){let x=0;for(let i=0;i<128;i++)x+=this.values[i]*o.values[i];return x}
  norm(){return Math.sqrt(this.dot(this))}
  toJSON(){return Array.from(this.values)}
}
export class LearningKernel128D {
  constructor(){this.model=new Vector128D();this.observations=0;this.events=[];}
  observe(values,target=0.0,rate=0.01){const x=new Vector128D(values);const err=target-this.model.dot(x)/(x.norm()||1);this.model=this.model.add(x.scale(rate*err));this.observations++;this.events.push({at:new Date().toISOString(),type:'observe',error:err});if(this.events.length>256)this.events.shift();return{error:err,observations:this.observations}}
  snapshot(){return{observations:this.observations,model:this.model.toJSON(),events:this.events}}
}
export class TrustStore { constructor(){this.nodes=new Map()} trust(node,score=1){this.nodes.set(node,Math.max(0,Math.min(1,score)))} isTrusted(node){return (this.nodes.get(node)??0)>0.5} list(){return Object.fromEntries(this.nodes)} }
export const DESKTOP_PROFILES=['aurora','fedora-gnome','ubuntu-gnome','debian-gnome','kde-plasma','xfce','cinnamon','mate','lxqt','gnome-flashback','safe-minimal','headless'];
export class BootDesktopProgress { constructor(){this.boot=0;this.desktop=0} setBoot(v){this.boot=Math.max(0,Math.min(100,v));if(this.boot<100)this.desktop=Math.min(this.desktop,99);return this.state()} setDesktop(v){if(this.boot<100)throw new Error('desktop loading is gated until boot reaches 100%');this.desktop=Math.max(0,Math.min(100,v));return this.state()} state(){return{boot:this.boot,desktop:this.desktop,complete:this.boot===100&&this.desktop===100}} }
export class ConcurrencyModel { constructor(){this.tasks=0;this.completed=0} submit(fn){this.tasks++;return Promise.resolve().then(fn).finally(()=>this.completed++)} state(){return{tasks:this.tasks,completed:this.completed,pending:this.tasks-this.completed}} }
