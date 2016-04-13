/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.kumars.touchid;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * An entity that represents a single transaction (purchase) of an item.
 */
public class Transaction {

    /**
     * The unique user ID who made the transaction
     */
    private final String mUserId;

    /**
     * The random long value that will be also signed by the private key and verified in the server
     * that the same nonce can't be reused to prevent replay attacks.
     */
    private final Long mClientNonce;

    public Transaction(String userId, long clientNonce) {
        mUserId = userId;
        mClientNonce = clientNonce;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(byteArrayOutputStream);
            dataOutputStream.writeUTF(mUserId);
            dataOutputStream.writeLong(mClientNonce);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
            } catch (IOException ignore) {
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException ignore) {
            }
        }
    }

    public String getUserId() {
        return mUserId;
    }

    public Long getClientNonce() {
        return mClientNonce;
    }
}
