package io.github.stalker2010.post;
import java.util.*;
import io.github.stalker2010.post.vm.*;

public final class VM {
		public static final VM current = new VM().initialize();
		public volatile int register = 0;
		public final List<OP> code = new ArrayList<OP>();
		public volatile boolean[] line = new boolean[0];
		public volatile int cpos = -1;
		public volatile int cline = Math.round(VMOptions.LINE_LIMIT / 2);
		public volatile VMState state = VMState.IDLE;
		volatile boolean stopFlag = false;
		public volatile boolean debugMode = false;
		public volatile StringBuilder log = new StringBuilder();
		public volatile String latestLine = "";
		public static final class VMOptions {
				public static final int OP_LIMIT = 10000;
				public static final int LINE_LIMIT = 1000;
		}
		public final List<OnVMStateChange> callbacks = new ArrayList<OnVMStateChange>();
		public static interface OnVMStateChange {
				public void onVMStateChange(VMState state);
		}
		public final enum VMState {
				IDLE, PREPARING, RUNNING, FINISHING, INTERRUPTED, OP_LIMIT_REACHED, BUSY_INTERNAL;
		}
		public VM() {
				log("VM Created");
		}
		public VM initialize() {
				if (line.length != (VMOptions.LINE_LIMIT + 1)) {
						line = new boolean[VMOptions.LINE_LIMIT + 1];
				}
				return this;
		}
		public boolean run() {
				if (!state.equals(VMState.IDLE)) return false;
				if (code.isEmpty()) return false;
				state = VMState.PREPARING;
				for (final OnVMStateChange c: callbacks) {
						c.onVMStateChange(state);
				}
				log("VM Started executing");
				state = VMState.RUNNING;
				for (final OnVMStateChange c: callbacks) {
						c.onVMStateChange(state);
				}
				cpos = 1;
				return continueRun();
		}
		public boolean continueRun() {
				int i = -1;
				while (i < VMOptions.OP_LIMIT) {
						i++;
						runLine();
						if (state.equals(VMState.INTERRUPTED)) {
								for (final OnVMStateChange c: callbacks) {
										c.onVMStateChange(state);
								}
								log("VM Interrupted");
								state = VMState.IDLE;
								return false;
						}
						if (stopFlag) {
								state = VMState.FINISHING;
								for (final OnVMStateChange c: callbacks) {
										c.onVMStateChange(state);
								}
								stopFlag = false;
								log("VM Ran OK");
								state = VMState.IDLE;
								return true;
						}
				}
				state = VMState.OP_LIMIT_REACHED;
				for (final OnVMStateChange c: callbacks) {
						c.onVMStateChange(state);
				}
				log("VM OP count > " + VMOptions.OP_LIMIT);
				return false;
		}
		public VM load(String str) {
				state = VMState.BUSY_INTERNAL;
				cpos = -1;
				register = 0;
				PostLinter.lint.messages.clear();
				initialize();
				code.clear();
				code.add(null);
				boolean containsStop = false;
				for (String s: str.split("\n")) {
						final String[] tokens = s.trim().split(" ");
						final String[] args = new String[tokens.length - 1];
						for (int i=1; i < tokens.length; i++) {
								args[i - 1] = tokens[i];
						}
						final String op = tokens[0];
						if (OP.isApplicableOp(op)) {
								code.add(new OP(op, args));
						} else if (IfOP.isApplicableOp(op)) {
								code.add(new IfOP(op, args));
						} else if (RegSetOp.isApplicableOp(op)) {
								code.add(new RegSetOp(op, args));
						} else if (StopOP.isApplicableOp(op)) {
								code.add(new StopOP(op, args));
								containsStop = true;
						} else {
								throw new RuntimeException("Unknown OP: " + op);
						}
				}
				log("VM Loaded code");
				if (!containsStop) {
						PostLinter.lint.post("[error] No stop statement.");
				}
				state = VMState.IDLE;
				for (final OnVMStateChange c: callbacks) {
						c.onVMStateChange(state);
				}
				return this;
		}
		public void runLine() {
				{
						final int cs = code.size();
						if ((cs <= cpos) || (cpos < 0)) {
								PostLinter.lint.post("[error] No such line defined");
								interruptVM();
								return;
						}
				}
				final OP cur = code.get(cpos);
				if (!cur.isCorrect()) {
						log("OP:" + cpos + ": incorrect");
				}
				final int cprev = cpos;
				cur.run();
				if (!debugMode) {
						StringBuilder ll = new StringBuilder();
						ll.append((cprev) + ": ");
						for (int i=cline - 10; i <= cline + 8; i++) {
								if ((i > 0) && (i < line.length)) {
										ll.append(line[i] ?"1": "0");
								}
						}
						log(ll.toString());
				}
		}
		public synchronized OP getCurrentOP() {
				{
						final int cs = code.size();
						if ((cs <= cpos) || (cpos < 0)) {
								return null;
						}
				}
				final OP cur = code.get(cpos);
				return cur;
		}
		public synchronized void stopVM() {
				stopFlag = true;
		}
		public synchronized void interruptVM() {
				state = VM.VMState.INTERRUPTED;
		}
		public synchronized void log(final String str) {
				log.append(str);
				log.append("\n");
				latestLine = str;
		}
}
