package com.chieffu.pocker.util;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CmdUtil {


    public static String execCommand(final boolean log, long timeout, File folder, Map<String, String> envParam, List<String> input, String... strCmd) {
        class ConsoleWatcher
                extends Thread {
            private final StringBuffer consoleOutput = new StringBuffer();
            private final InputStream input;

            ConsoleWatcher(InputStream input) {
                this.input = input;
            }

            public void run() {
                InputStreamReader isr = null;
                try {
                    isr = new InputStreamReader(this.input, "GBK");
                } catch (Exception e1) {
                    isr = new InputStreamReader(this.input);
                }
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                try {
                    while ((line = br.readLine()) != null) {
                        if (log) {
                            System.out.println(line);
                        }
                        this.consoleOutput.append(line).append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null)
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

            String getConsoleOutput() {
                return this.consoleOutput.toString();
            }
        }
      class Worker extends Thread {
            private final Process process;
            private Integer exit;
            private final List<String> input;

            Worker(Process process, List<String> input) {
                this.process = process;
                this.input = input;
            }

            public void run() {
                try {
                    if (this.input != null && this.input.size() > 0) {
                        OutputStreamWriter writer = new OutputStreamWriter(this.process.getOutputStream());

                        try {
                            for (int i = 0; i < this.input.size(); i++) {
                                writer.write(this.input.get(i));
                                writer.write(10);
                                writer.flush();
                            }
                        } catch (IOException iOException) {
                        } finally {
                            if (writer != null) {
                                try {
                                    writer.close();
                                } catch (IOException iOException) {
                                }
                            }
                        }

                    }


                    this.exit = Integer.valueOf(this.process.waitFor());
                } catch (InterruptedException ignore) {
                    return;
                }
            }
        }
      String osName = System.getProperty("os.name");
        List<String> cmds = new ArrayList<>();
        if (osName.toLowerCase().contains("windows")) {
            if (osName.contains("95") || osName.contains("98")) {
                cmds.add("command.com");
            } else {
                cmds.add(System.getenv("SystemRoot") + "/System32/cmd.exe");
            }
            cmds.add("/C");
        } else {
            cmds.add("sh");
            cmds.add("-c");
        }
        for (String s : strCmd) {
            cmds.add(s);
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 2; i < cmds.size(); i++) {
            sb.append(cmds.get(i)).append(" ");
        }
        String cmdLine = (sb.length() > 0) ? sb.substring(0, sb.length() - 1) : "";
        ProcessBuilder pb = new ProcessBuilder(cmds);
        pb.directory(folder);
        Map<String, String> env = pb.environment();
        if (envParam != null)
            env.putAll(envParam);
        Process p = null;
        try {
            if (log) {
                CmdUtil.log.info(folder.getAbsoluteFile().getCanonicalPath() + ">" + cmdLine);
            }
            p = pb.start();
            ConsoleWatcher consoleWatcher = new ConsoleWatcher(p.getInputStream());
            consoleWatcher.start();
            if (timeout > 0L) {
                Worker worker = new Worker(p, input);
                worker.start();
                worker.join(timeout);
            } else {
                p.waitFor();
            }
            consoleWatcher.join();
            String result = consoleWatcher.getConsoleOutput();
            if (log) {
                CmdUtil.log.info(result);
            }
            return result;
        } catch (Exception exception) {

        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return "";
    }

    private static void killProcess(String[] strCmd) {
        String pid = "";
        String[] ss = strCmd[0].split("\\s+")[0].split("\\\\|/");
        String cmd = ss[ss.length - 1];
        do {
            String tasklist = exeCommandTaskList();
            pid = PatternUtils.matchFirst(tasklist, "(?i)" + cmd + "(.exe)?\\s+(\\d+)", 2);
            log.info(execCommand(3000L, new String[]{"taskkill /F /pid " + pid}));
        } while (pid.length() > 0);
    }

    public static String execCommand(File folder, Map<String, String> envParam, String... cmdStr) {
        return execCommand(false, -1L, folder, envParam, null, cmdStr);
    }

    public static String execCommand(File folder, List<String> inputs, String... cmdStr) {
        return execCommand(false, -1L, folder, null, inputs, cmdStr);
    }

    public static String execCommand(List<String> inputs, String... cmdStr) {
        return execCommand(false, -1L, new File("."), new HashMap<>(), inputs, cmdStr);
    }

    public static String execCommand(String... cmdStr) {
        return execCommand(false, -1L, new File("."), new HashMap<>(), null, cmdStr);
    }

    public static String execCommand(long timeout, String... cmdStr) {
        return execCommand(false, timeout, new File("."), new HashMap<>(), null, cmdStr);
    }

    public static String execCommand(boolean log, long timeout, String... cmdStr) {
        return execCommand(log, timeout, new File("."), new HashMap<>(), null, cmdStr);
    }

    public static String execCommand(boolean log, String... cmdStr) {
        return execCommand(log, -1L, new File("."), new HashMap<>(), null, cmdStr);
    }

    public static String execCommand(long timeout, File folder, String... cmdStr) {
        return execCommand(false, timeout, folder, new HashMap<>(), null, cmdStr);
    }

    public static String execCommand(File folder, String... cmdStr) {
        return execCommand(false, -1L, folder, new HashMap<>(), null, cmdStr);
    }

    public static String execCommand(boolean log, long timeout, File folder, List<String> inputs, String... cmdStr) {
        return execCommand(log, timeout, folder, new HashMap<>(), inputs, cmdStr);
    }

    public static String execCommand(boolean log, File folder, String... cmdStr) {
        return execCommand(log, -1L, folder, new HashMap<>(), null, cmdStr);
    }

    public static String exeCommandTaskList() {
        return execCommand(false, System.getenv("SystemRoot") + "/System32/tasklist.exe");
    }

    public static boolean isWindows() {
        return System.getProperty("os.name", "WINDOWS XP").toUpperCase().contains("WINDOWS");
    }

    public static boolean is64bitWindows() {
        if (isWindows()) {
            String pgrm86 = System.getenv("ProgramFiles(x86)");
            return (pgrm86 != null && pgrm86.length() > 0);
        }
        return false;
    }


    public static String getMotherboardSN() {
        File file = null;
        File exe = null;
        try {
            file = File.createTempFile("tmp", ".vbs");
            FileWriter fw = new FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\nSet colItems = objWMIService.ExecQuery _ \n   (\"Select * from Win32_BaseBoard\") \nFor Each objItem in colItems \n    Wscript.Echo objItem.SerialNumber \n    exit for  ' do the first cpu only! \nNext \n";


            fw.write(vbs);
            fw.close();
            return execCommand(false, System.getenv("SystemRoot") + "/System32/cscript.exe", "//NoLogo", file.getPath().trim()).trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null && file.exists())
                file.delete();
        }
        return "";
    }


    public static String getHardDiskSN(String drive) {
        File file = null;
        File exe = null;
        try {
            file = File.createTempFile("tmp", ".vbs");
            FileWriter fw = new FileWriter(file);

            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\nSet colDrives = objFSO.Drives\nSet objDrive = colDrives.item(\"" + drive + "\")\nWscript.Echo objDrive.SerialNumber";


            fw.write(vbs);
            fw.close();
            return execCommand(false, System.getenv("SystemRoot") + "/System32/cscript.exe", "//NoLogo", file.getPath().trim()).trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null && file.exists())
                file.delete();
            if (exe != null && exe.exists())
                exe.delete();
        }
        return "";
    }


    public static String getCPUSerial() {
        File file = null;
        File exe = null;
        try {
            file = File.createTempFile("tmp", ".vbs");
            FileWriter fw = new FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\nSet colItems = objWMIService.ExecQuery _ \n   (\"Select * from Win32_Processor\") \nFor Each objItem in colItems \n    Wscript.Echo objItem.ProcessorId \n    exit for  ' do the first cpu only! \nNext \n";


            fw.write(vbs);
            fw.close();
            return execCommand(false, System.getenv("SystemRoot") + "/System32/cscript.exe", "//NoLogo", file.getPath().trim()).trim();
        } catch (Exception e) {
            e.fillInStackTrace();
        } finally {
            if (file != null && file.exists())
                file.delete();
        }
        return "";
    }

    public static String getCurrentProgressName() {
        int pid = getPid();
        String logs = exeCommandTaskList();
        return PatternUtils.matchFirst(logs, "(?i)([^\\s]*\\.exe).*?[\\s:]+" + pid + "\\s", 1);
    }

    public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getIP(String host) {
        String log = execCommand("ping", host, "-n", "1");
        return PatternUtils.matchFirst(log, "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}", 0);
    }


    public static void main(String[] args) {
        System.out.println(execCommand(15000L, "ping  www.baidu.com"));
        log.info(execCommand(true, "rasdial vpn vpn189 HY74UE26 /PHONE:107.163.157.190"));
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\CmdUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */