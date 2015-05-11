package io.github.stalker2010.post.vm;
import io.github.stalker2010.post.*;

public class OP implements Runnable {
		public final String[] args;
		public final String op;
		public OP(final String op, final String[] args) {
				this.op = op;
				this.args = args;
		}
		private static final String[] ops = new String[] {
				"<", ">", "M", "1", "X", "0", "++", "--"
		};
		public static boolean isApplicableOp(final String op) {
				for (int i=0; i < 8; i++) {
						if (op.equals(ops[i])) {
								return true;
						}
				}
				return false;
		}
		public void shiftLeft() {
				final VM v = VM.current;
				v.cline--;
				if (v.cline < 1) {
						v.cline = 1;
						PostLinter.lint.post("[warn] Left line limit exceeded");
				}
		}
		public void shiftRight() {
				final VM v = VM.current;
				v.cline++;
				if (v.cline > VM.VMOptions.LINE_LIMIT) {
						v.cline = VM.VMOptions.LINE_LIMIT;
						PostLinter.lint.post("[warn] Right line limit exceeded");
				}
		}
		public void stop() {
				final VM v = VM.current;
				v.stopVM();
		}
		public void mark() {
				final VM v = VM.current;
				if (!v.line[v.cline]) {
						v.line[v.cline] = true;
				} else {
						PostLinter.lint.post("[perf] Cell is already 1");
				}
		}
		public void unmark() {
				final VM v = VM.current;
				if (v.line[v.cline]) {
						v.line[v.cline] = false;
				} else {
						PostLinter.lint.post("[perf] Cell is already 0");
				}
		}
		public void registerAdd() {
				final VM v = VM.current;
				v.register++;
		}
		public void registerSubstract() {
				final VM v = VM.current;
				v.register--;
		}
		public void registerSet(int value) {
				final VM v = VM.current;
				v.register = value;
		}
		public boolean isCorrect() {
				return true;
		}
		public void gotoLine(final int id) {
				final VM v = VM.current;
				if (v.cpos == id) {
						PostLinter.lint.post("[warn] GoTo the same line. Will fall into infinite cycle. Went to next line instead.");
						v.cpos++;
				} else {
						v.cpos = id;
				}
		}
		public void gotoNextLine() {
				final VM v = VM.current;
				v.cpos++;
		}

		@Override
		public void run() {
				if (op.equals("<")) {
						shiftLeft();
				} else if (op.equals(">")) {
						shiftRight();
				} else if (op.equals("++")) {
						registerAdd();
				} else if (op.equals("--")) {
						registerSubstract();
				} else if (op.equals("M") || op.equals("1")) {
						mark();
				} else if (op.equals("X") || op.equals("0")) {
						unmark();
				}
				if (args.length == 1) {
						try {
								gotoLine(Integer.valueOf(args[0]));
						} catch (NumberFormatException e) {
								e.printStackTrace();
								PostLinter.lint.post("[error] Next line number is not a number");
								VM.current.interruptVM();
						}
				} else {
						gotoNextLine();
				}
		}
}
