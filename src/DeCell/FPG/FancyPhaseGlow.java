package DeCell.FPG;

import com.fs.starfarer.api.Global;
import org.apache.log4j.Priority;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.opengl.GL45.*;
// i would global static these if java had that feature

public class FancyPhaseGlow {
    public static boolean Debug = false;

    public static void Log(String s) {
        Global.getLogger(FancyPhaseGlow.class).log(Priority.INFO, s);
    }
    public static void LogWarn(String s){
        Global.getLogger(FancyPhaseGlow.class).log(Priority.WARN, s);
    }
    public static void LogErr(String s){
        Global.getLogger(FancyPhaseGlow.class).log(Priority.ERROR, s);
    }
}
