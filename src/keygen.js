import { generateKeyPairSync, createHash, createHmac, randomBytes } from 'node:crypto';
export function generateEd25519(){const kp=generateKeyPairSync('ed25519');return{algorithm:'Ed25519',publicKey:kp.publicKey.export({type:'spki',format:'der'}).toString('base64'),privateKey:kp.privateKey.export({type:'pkcs8',format:'der'}).toString('base64')}}
export function hash(data,algorithm='sha256'){return createHash(algorithm).update(data).digest('hex')}
export function hmac(data,key,algorithm='sha256'){return createHmac(algorithm,key).update(data).digest('hex')}
export function randomHex(bytes=32){return randomBytes(bytes).toString('hex')}
