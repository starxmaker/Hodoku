export declare function executeCommand(
  commandParts: string[],
  onNewLine?: (line: string) => void,
): Promise<string[]>;
