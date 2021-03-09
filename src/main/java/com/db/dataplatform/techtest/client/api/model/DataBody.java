package com.db.dataplatform.techtest.client.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@JsonSerialize(as = DataBody.class)
@JsonDeserialize(as = DataBody.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class DataBody {

    @NotNull
    private String dataBody;

    private String checksum;

    public DataBody(@NotNull String dataBody) throws NoSuchAlgorithmException {
        this.dataBody = dataBody;
        this.checksum = getMD5Hash(dataBody);
    }

    private String getMD5Hash(String msg) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return DatatypeConverter.printHexBinary(md.digest(msg.getBytes())).toUpperCase();
    }

    @Override
    public String toString() {
        return "DataBody{" +
                "dataBody='" + dataBody + '\'' +
                '}';
    }
}
