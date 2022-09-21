package org.dulab.adapcompounddb.site.services.utils;

import org.mindrot.jbcrypt.BCrypt;

public class bcrypt {
    private static final int HASHING_LOG_ROUNDS = 10;
    public static void main (String args[]){
        String salt = BCrypt.gensalt(HASHING_LOG_ROUNDS);

        System.out.println(BCrypt.hashpw("tritoanh", salt));
    }
}
