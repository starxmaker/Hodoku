export interface ExecuteCommandControl {
  cancel(): void;
  readonly cancelled: boolean;
}

export interface ExecuteCommandOptions {
  onNewLine?: (line: string, control: ExecuteCommandControl) => boolean | void;
  signal?: AbortSignal;
}

export interface RuntimePoolOptions {
  size?: number;
}

export interface RuntimePool {
  executeCommand(
    commandParts: string[],
    onNewLineOrOptions?: ((line: string, control: ExecuteCommandControl) => boolean | void) | ExecuteCommandOptions,
  ): Promise<string[]>;
  dispose(): void;
}

export declare function createRuntimePool(size: number): RuntimePool;
export declare function createRuntime(): RuntimePool;
