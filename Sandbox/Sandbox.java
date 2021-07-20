package Sandbox;

import Source.*;

final class Sandbox 
{
    public static void main(String[] args)
    {
        NetlabUT[] unitList = { new Formatting() };

        GradeForm gf = new GradeForm("Modul 1 - Praktikum Pemrograman Berorientasi Obyek", unitList);
        gf.setVisible(true);
    }
}