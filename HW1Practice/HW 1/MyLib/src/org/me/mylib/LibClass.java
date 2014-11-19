/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.me.mylib;

/**
 *
 * @author Yukti
 */
public class LibClass {

    public static String acrostic(String[] args) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            if (args[i].length() > i) {
                b.append(args[i].charAt(i));
            } else {
                b.append('?');
            }
        }
        return b.toString();
    }
}
