package io.amerhwitat.cpu4096simulator;

import java.util.Arrays;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX workbench for the repository's host-side simulator model.
 * This is not a hardware emulator and does not imply complete ISA coverage.
 */
public final class Cpu4096FxApp extends Application {
    private final TextArea assembly = new TextArea(
        "MOV R1, R0\nADD R2, R1, R1\nADD R3, R2, R2");
    private final TextArea output = new TextArea();
    private final Label status = new Label("Host-side model · 8192-bit profile · limited implemented opcode set");

    @Override
    public void start(Stage stage) {
        stage.setTitle("CPU4096Simulator · JavaFX Workbench");
        assembly.setPromptText("Enter assembler source…");
        assembly.setPrefRowCount(12);
        output.setEditable(false);
        output.setWrapText(false);
        Button run = new Button("Assemble + Run");
        run.setDefaultButton(true);
        run.setOnAction(event -> runProgram());
        Button example = new Button("Load example");
        example.setOnAction(event -> assembly.setText(
            "start: MOV R1, R0\nADD R2, R1, R1\nCMP R2, R0\nJNZ done\nADD R3, R2, R2\ndone: BARRIER"));
        HBox actions = new HBox(8, run, example);
        VBox left = new VBox(8, new Label("Assembly source"), assembly, actions);
        VBox right = new VBox(8, new Label("Execution report"), output);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        VBox.setVgrow(assembly, Priority.ALWAYS);
        VBox.setVgrow(output, Priority.ALWAYS);
        left.setPadding(new Insets(12));
        right.setPadding(new Insets(12));
        left.setPrefWidth(500);
        right.setPrefWidth(500);
        HBox work = new HBox(8, left, right);
        BorderPane root = new BorderPane(work);
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(8, 12, 12, 12));
        stage.setScene(new Scene(root, 1100, 720));
        stage.show();
    }

    private void runProgram() {
        try {
            byte[] machineCode = Assembler.assemble(assembly.getText());
            if (machineCode.length == 0) throw new IllegalArgumentException("Program has no instructions.");
            if (machineCode.length % InstructionCodec.INSTRUCTION_BYTES != 0)
                throw new IllegalStateException("Assembler emitted a partial instruction.");
            CpuCore cpu = new CpuCore(8192, 1024);
            int steps = 0;
            final int maxSteps = 1000;
            while (cpu.pc() >= 0 && cpu.pc() < machineCode.length && steps < maxSteps) {
                int pc = Math.toIntExact(cpu.pc());
                if (pc % InstructionCodec.INSTRUCTION_BYTES != 0
                    || pc + InstructionCodec.INSTRUCTION_BYTES > machineCode.length) {
                    throw new IllegalStateException("Program counter points outside an aligned instruction.");
                }
                byte[] instructionBytes = Arrays.copyOfRange(machineCode, pc, pc + InstructionCodec.INSTRUCTION_BYTES);
                cpu.execute(InstructionCodec.decode(instructionBytes));
                steps++;
            }
            StringBuilder report = new StringBuilder();
            report.append("Mode: deterministic host-side semantic model\n")
                .append("Width: 8192 bits (research profile; not physical CPU width)\n")
                .append("Encoded bytes: ").append(machineCode.length).append("\n")
                .append("Steps: ").append(steps).append("\n")
                .append("PC: ").append(cpu.pc()).append("\n")
                .append("Flags: ").append(cpu.flags()).append("\n\nRegisters R0–R15 (hex, shortened to 64 low bits):\n");
            var registers = cpu.registers();
            for (int i = 0; i < 16; i++) {
                String hex = registers[i].toBigInteger().toString(16);
                String low = hex.length() > 16 ? hex.substring(hex.length() - 16) : hex;
                report.append(String.format("R%-2d 0x%s%n", i, low));
            }
            if (steps == maxSteps) report.append("\nStopped at the safety step limit; a branch may be looping.\n");
            if (cpu.pc() >= machineCode.length) report.append("\nExecution reached the end of the assembled byte buffer.\n");
            output.setText(report.toString());
            status.setText("Finished · " + steps + " instruction steps · model/ISA coverage remains partial");
        } catch (Exception error) {
            status.setText("Assembly or execution failed.");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CPU4096Simulator");
            alert.setHeaderText("Could not execute program");
            alert.setContentText(error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage());
            alert.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
