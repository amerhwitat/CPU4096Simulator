import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
const catalog=JSON.parse(fs.readFileSync(new URL('../public/installer_catalog.json',import.meta.url),'utf8'));
const registry=JSON.parse(fs.readFileSync(new URL('../public/aurora_apps.json',import.meta.url),'utf8'));
test('installer catalog has Linux Windows and Chimera formats',()=>{const ids=new Set(catalog.package_formats.map(x=>x.id));for(const x of ['deb','rpm','pacman','flatpak','winget','msix','chimera-pkg'])assert.ok(ids.has(x))});
test('simulator desktop exposes Installer Manager',()=>{const apps=registry.categories.flatMap(c=>c.apps);assert.ok(apps.some(a=>a.id==='installer-manager'&&a.url==='installer_manager.html'))});
