package io.github.stalker2010.post.vm;
import io.github.stalker2010.post.*;

public class IfOP extends OP {
		public IfOP(final String op, final String[] args) {
				super(op, args);
		}

		@Override
		public static boolean isApplicableOp(String op) {
				return op.equals("?") || op.equals("IF");
		}

		@Override
		public boolean isCorrect() {
				final int as = args.length;
				return (as > 0) && (as <= 3);
		}

		public int conformsRegIF(String arg) {
				if (arg.length() < 2) return -1;
				final char op = arg.charAt(0);
				if (Character.isDigit(op)) {
						return -1;
				}
				final char op1 = arg.charAt(1);
				Long l;
				try {
						if (Character.isDigit(op1)) {
								l = Long.valueOf(arg.substring(1));
						} else {
								l = Long.valueOf(arg.substring(2));
						}
				} catch (NumberFormatException e) {
						e.printStackTrace();
						PostLinter.lint.post("[error] Wrong IF condition number");
						VM.current.interruptVM();
						return -1;
				}
				int reg = VM.current.register;
				switch (op) {
						case '=':
								return (reg == l) ?1: 0;
						case '<':
								if (reg == l) {
										if (op1 == '=') {
												return 1;
										} else {
												PostLinter.lint.post("[hint] It equals. Maybe, you want <=?");
										}
								}
								return (reg < l) ?1: 0;
						case '>':
								if (reg == l) {
										if (op1 == '=') {
												return 1;
										} else {
												PostLinter.lint.post("[hint] It equals. Maybe, you want >=?");
										}
								}
								return (reg > l) ?1: 0;
						case '~':
								return (reg != l) ?1: 0;
						default:
								return -1;
				}
		}
		private int getNextLine() {
				final int as = args.length;
				// >6 4 5
				// =6 4 --If true
				// 4 5
				// 4 --If 1
				int cfms = conformsRegIF(args[0]);
				if (as == 3) {
						if (cfms == 1) {
								return Integer.valueOf(args[2]);
						} else if (cfms == 0) {
								return Integer.valueOf(args[1]);
						} else {
								PostLinter.lint.post("[error] Unexpected IF branch in IFOp");
								VM.current.interruptVM();
								return -1;
						}
				} else {
						if (cfms == -1) {
								final boolean isRight = VM.current.line[VM.current.cline];
								final Integer firstVariant = Integer.valueOf(args[0]);
								if (as == 1) {
										return isRight ? firstVariant: -1;
								} else {
										final int rl = Integer.valueOf(args[1]);
										return isRight ? rl: firstVariant;
								}
						} else {
								if (cfms == 1) {
										return Integer.valueOf(args[1]);
								} else {
										return -1;
								}
						}
				}
		}

		@Override
		public void run() {
				int nl = getNextLine();
				if (nl == -1) {
						gotoNextLine();
				} else {
						gotoLine(nl);
				}
		}
}
