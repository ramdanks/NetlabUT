package com.NetlabUT;

import javax.swing.*;

public class ObjectDetail extends JFrame
{
    public ObjectDetail(Object object)
    {
        setTitle(Profile.getObjectIdentifierString(object));
        setVisible(true);
    }
}
