import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';

const desktop = JSON.parse(fs.readFileSync('public/desktop_profiles.json', 'utf8'));
const retro = JSON.parse(fs.readFileSync('public/retro_emulators.json', 'utf8'));

test('desktop profiles include Aurora, Linux and Windows modes', () => {
  for (const id of ['aurora-native','linux-gnome','linux-kde-plasma','linux-xfce','windows-11']) {
    assert.ok(desktop.profiles.some(p => p.id === id));
  }
});

test('retro catalog covers major classic computer families', () => {
  const systems = new Set(retro.emulators.flatMap(e => e.systems));
  for (const name of ['C64','Amiga','Apple II','ZX Spectrum','MSX','DOS','Atari ST']) assert.ok(systems.has(name), name);
});
