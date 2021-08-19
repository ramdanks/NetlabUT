package com.Standard;

public enum FileType
{
    JSON_KEY_FILE_TYPE("netlab_file_type");

    private String string = null;
    private FileType(String string)  { this.string = string; }
    public String toString()         { return string; }
}
