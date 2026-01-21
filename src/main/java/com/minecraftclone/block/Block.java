package com.minecraftclone.block;

import GLOOP.GLWuerfel;

public abstract class Block {

    public Block(double x, double y, double z, double width, String texture) {
        var block = new GLWuerfel(x, y, z, width, texture);
    }
}
