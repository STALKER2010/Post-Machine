package io.github.stalker2010.post.vm;

public class RegSetOp extends OP {
		public RegSetOp(final String op, final String[] args) {
				super(op, args);
		}

		@Override
		public static boolean isApplicableOp(String op) {
				return op.equals("SET");
		}

		@Override
		public boolean isCorrect() {
				final int as = args.length;
				return (as == 1) && (as == 2);
		}

		@Override
		public void run() {
				registerSet(Integer.valueOf(args[0]));
				if (args.length == 2) {
						gotoLine(Integer.valueOf(args[1]));
				} else {
						gotoNextLine();
				}
		}
}
