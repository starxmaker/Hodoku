import { copyFile, access } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const scriptDir = path.dirname(fileURLToPath(import.meta.url));
const packageDir = path.resolve(scriptDir, '..');
const repoRoot = path.resolve(packageDir, '..', '..');
const sourceBundle = path.join(repoRoot, 'target', 'teavm-js', 'Hodoku-teavm.js');
const destinationBundle = path.join(packageDir, 'Hodoku-teavm.cjs');

try {
  await access(sourceBundle);
} catch (error) {
  throw new Error(
    `TeaVM bundle not found at ${sourceBundle}. Run "mvn clean package" from the repository root first.`
  );
}

await copyFile(sourceBundle, destinationBundle);