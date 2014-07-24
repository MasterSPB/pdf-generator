package ru.onyx.clipper.utils;

import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by novikam on 18.07.14.
 */
public class ReportCalcUtils {



    public static double brCalculate(String str) {
        String keyBr;
        Stack<Double> br = new Stack<Double>();
        double resultBr=0;
        StringTokenizer stBr = new StringTokenizer(str, " +-*/", true);
        while (stBr.hasMoreElements()) {
            keyBr = stBr.nextToken();
            if (keyBr.equals("+")) {
            } else if (keyBr.equals("-")) {
                double x1 = Double.parseDouble(stBr.nextToken());
                br.push(-x1);
            } else if (keyBr.equals("*")) {
                double x1 = br.pop();
                double x2 = Double.parseDouble(stBr.nextToken());
                br.push(x1 * x2);
            } else if (keyBr.equals("/")) {
                double x1 = br.pop();
                double x2 = Double.parseDouble(stBr.nextToken());
                br.push(x1 / x2);
            } else if (keyBr.equals(" ")) {
            } else {
                br.push(Double.parseDouble(keyBr));
            }
        }
        do {
            resultBr += br.pop();
        } while (!br.empty());
        return resultBr;
    }

    public static double calculate(String equation) {
        Stack<Double> vals = new Stack<Double>();
        double result=0;
        String key;
        String keyT;
        String brStr = "";
        StringTokenizer st = new StringTokenizer(equation, "() +-/*", true);
        while (st.hasMoreElements()) {
            key = st.nextToken();
            if (key.equals("+")) {
            }
            else if (key.equals("-")) {
                keyT = st.nextToken();
                if (keyT.equals("(")) {
                    do {
                        keyT = st.nextToken();
                        if (!keyT.equals(")")) {
                            brStr = brStr + keyT;
                        } else {
                            break;
                        }
                    } while (true);
                    double x1 = brCalculate(brStr);
                    brStr = "";
                    vals.push(-x1);
                } else {
                    double x1 = Double.parseDouble(keyT);
                    vals.push(-x1);
                }

            } else if (key.equals("*")) {
                double x1 = vals.pop();
                keyT = st.nextToken();
                if (keyT.equals("(")) {
                    do {
                        keyT = st.nextToken();
                        if (!keyT.equals(")")) {
                            brStr = brStr + keyT;
                        } else {
                            break;
                        }
                    } while (true);
                    double x2 = brCalculate(brStr);
                    vals.push(x1 * x2);
                    brStr = "";
                } else {
                    double x2 = Double.parseDouble(keyT);
                    vals.push(x1 * x2);
                }

            } else if (key.equals("/")) {
                double x1 = vals.pop();
                keyT = st.nextToken();
                if (keyT.equals("(")) {
                    do {
                        keyT = st.nextToken();
                        if (!keyT.equals(")")) {
                            brStr = brStr + keyT;
                        } else {
                            break;
                        }
                    } while (true);
                    double x2 = brCalculate(brStr);
                    vals.push(x1 / x2);
                    brStr = "";
                } else {
                    double x2 = Double.parseDouble(keyT);
                    vals.push(x1 / x2);
                }

            } else if (key.equals(" ")) {
            } else if (key.equals("(")) {
                do {
                    keyT = st.nextToken();
                    if (!keyT.equals(")")) {
                        brStr = brStr + keyT;
                    } else {
                        break;
                    }
                } while (true);
                vals.push(brCalculate(brStr));
                brStr = "";
            } else {
                vals.push(Double.parseDouble(key));
            }
        }

        do {
            result += vals.pop();
        } while (!vals.empty());



        return result;

    }

}


