package me.botsko.prism.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.botsko.prism.testHelpers.TestHelper;
import me.botsko.prism.utils.block.Utilities;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 23/05/2020.
 */
class UtilitiesTest {

    /**
     * Required to avoid NPE.
     */
    private ServerMock server;

    @BeforeEach
    public void setUp() {
        server = TestHelper.setup();
    }
    
    @Test
    public void testAreBlockIdsSameCoreItem() {
        Material m1 = Material.DIRT;
        Material m2 = Material.DIRT;
        assertTrue(Utilities.areBlockIdsSameCoreItem(m1,m2));
        m1 = Material.GRASS_BLOCK;
        assertTrue(Utilities.areBlockIdsSameCoreItem(m1,m2));
    }



}