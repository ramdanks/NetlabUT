package com.NetlabUT;

import javax.swing.*;

public class ThrowableDetail extends JFrame
{
    public ThrowableDetail(Object object)
    {
        setTitle(Profile.getObjectIdentifierString(object));
        setVisible(true);
    }
}
