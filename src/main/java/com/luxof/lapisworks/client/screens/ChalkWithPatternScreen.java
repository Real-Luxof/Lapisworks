package com.luxof.lapisworks.client.screens;

import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;

import static com.luxof.lapisworks.Lapisworks.closeEnough;
import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.LapisworksIDs.SET_PATTERNS_ON_CHALK;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import org.joml.Matrix4f;
import org.joml.Vector2i;

public class ChalkWithPatternScreen extends HandledScreen<ChalkWithPatternScreenHandler> {
    private static final Identifier TEXTURE = id("textures/gui/chalk_with_pattern.png");

    private final List<Vector2i> gridSetup = List.of(
        new Vector2i(27, 22), new Vector2i(62, 22), new Vector2i(96, 22), new Vector2i(131, 22), new Vector2i(166, 22), new Vector2i(200, 22), new Vector2i(235, 22),
        new Vector2i(10, 52), new Vector2i(44, 52), new Vector2i(79, 52), new Vector2i(114, 52), new Vector2i(148, 52), new Vector2i(183, 52), new Vector2i(218, 52),
        new Vector2i(27, 82), new Vector2i(62, 82), new Vector2i(96, 82), new Vector2i(131, 82), new Vector2i(166, 82), new Vector2i(200, 82), new Vector2i(235, 82),
        new Vector2i(10, 112), new Vector2i(44, 112), new Vector2i(79, 112), new Vector2i(114, 112), new Vector2i(148, 112), new Vector2i(183, 112), new Vector2i(218, 112),
        new Vector2i(27, 141), new Vector2i(62, 141), new Vector2i(96, 141), new Vector2i(131, 141), new Vector2i(166, 141), new Vector2i(200, 141), new Vector2i(235, 141),
        new Vector2i(10, 171), new Vector2i(44, 171), new Vector2i(79, 171), new Vector2i(114, 171), new Vector2i(148, 171), new Vector2i(183, 171), new Vector2i(218, 171),
        new Vector2i(27, 202), new Vector2i(62, 202), new Vector2i(96, 202), new Vector2i(131, 202), new Vector2i(166, 202), new Vector2i(200, 202), new Vector2i(235, 202),
        new Vector2i(10, 232), new Vector2i(44, 232), new Vector2i(79, 232), new Vector2i(114, 232), new Vector2i(148, 232), new Vector2i(183, 232), new Vector2i(218, 232)
    );
    private List<Vector2i> grid;
    // how far does the mouse need to be for the line snap to the grid?
    private final long distanceBeforeSnapToGridSqr = 50;
    // how far can the mouse be from it's most recent dot before the line stops snapping to the grid?
    private final long distanceFromRecentPointBeforeStopSnappingSqr = 2700;
    public final int patternLimit = 5;
    private int x;
    private int y;
    public ChalkWithPatternScreen(
        ChalkWithPatternScreenHandler handler,
        PlayerInventory inventory,
        Text title
    ) {
        super(handler, inventory, title);
        super.backgroundHeight = 256;
        super.backgroundWidth = 256;
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        this.grid = gridSetup.stream().map(vec -> new Vector2i(x + vec.x, y + vec.y)).toList();
    }

    private Pair<Long, Vector2i> findClosestPointTo(int x, int y) {
        Vector2i xy = new Vector2i(x, y);

        Vector2i closest = grid.get(0);
        long closestDistance = 99999999;

        for (Vector2i point : grid) {
            long distance = point.distanceSquared(xy);
            if (distance < closestDistance) {
                closest = point;
                closestDistance = distance;
            }
        }

        return new Pair<>(closestDistance, closest);
    }

    private List<Vector2i> currentlyEngaged = new ArrayList<>();
    private List<List<Vector2i>> patterns = new ArrayList<>();
    private int lineSize = 4;

    private void drawLine(
        BufferBuilder buffer,
        Matrix4f posMat,
        float x1, float y1,
        float x2, float y2,
        float thickness,
        int r, int g, int b, int a
    ) {
        float halfThickness = thickness * 0.5f;
        x1 = x1-halfThickness+1f;
        x2 = x2-halfThickness+1f;
        y1 = y1-halfThickness+1f;
        y2 = y2-halfThickness+1f;
        boolean goingHorizontal = closeEnough(y2, y1, 0.0001f);
        boolean goingDown = y2 > y1;
        float offX1 = 0f;
        float offX2 = 0f;

        if (x2 > x1) {
            offX1 = goingHorizontal ? halfThickness : goingDown ? thickness : 0f;
            offX2 = goingHorizontal ? halfThickness : goingDown ? 0f : thickness;
        } else if (x2 < x1) {
            offX1 = goingHorizontal ? halfThickness : goingDown ? 0f : thickness;
            offX2 = goingHorizontal ? halfThickness : goingDown ? thickness : 0;
        }

        float z = 0f;
        buffer.vertex(posMat, x1+offX1, y1, z).color(r, g, b, a).next();
        buffer.vertex(posMat, x1+offX2, y1+thickness, z).color(r, g, b, a).next();
        buffer.vertex(posMat, x2+offX2, y2+thickness, z).color(r, g, b, a).next();
        buffer.vertex(posMat, x2+offX1, y2, z).color(r, g, b, a).next();
    }

    private boolean pointIsOccupied(Vector2i point) {
        for (List<Vector2i> pattern : patterns) {
            if (pattern.contains(point)) return true;
        }
        return false;
    }

    private boolean lineIsOccupied(Vector2i A, Vector2i B) {
        List<List<Vector2i>> allPatterns = new ArrayList<>(patterns);
        allPatterns.add(currentlyEngaged);

        for (List<Vector2i> pattern : allPatterns) {
            Vector2i contenderA = pattern.get(0);
            for (Vector2i contenderB : pattern.subList(1, pattern.size())) {

                if (A.equals(contenderA) && B.equals(contenderB) ||
                    B.equals(contenderA) && A.equals(contenderB))
                    return true;

                contenderA = contenderB;
            }
        }
        return false;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
        this.grid = gridSetup.stream().map(vec -> new Vector2i(x + vec.x, y + vec.y)).toList();


        drawBackground(context, delta, mouseX, mouseY);


        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.lineWidth(lineSize);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        MatrixStack matrices = context.getMatrices();
        Matrix4f posMat = matrices.peek().getPositionMatrix();


        List<List<Vector2i>> allPatterns = new ArrayList<>(patterns);

        List<Vector2i> engagedRn = new ArrayList<>(currentlyEngaged);
        engagedRn.add(new Vector2i(Math.round(mouseX), Math.round(mouseY)));
        if (engagedRn.size() != 0) allPatterns.add(engagedRn);


        for (List<Vector2i> pattern : allPatterns) {
            Vector2i prev = pattern.get(0);

            for (Vector2i curr : pattern.subList(1, pattern.size())) {

                drawLine(
                    buffer, posMat,
                    prev.x, prev.y,
                    curr.x, curr.y,
                    5f,
                    251, 201, 227, 255
                );
                prev = curr;

            }
        }


        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.enableCull();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double mX, double mY, int button) {
        if (button != 0 || patterns.size() >= patternLimit) return false;

        // O(1) trust (exception triggered rarely)
        // this "temporary solution" will last forever vro :broken_heart:
        for (int i = patterns.size() - 1; i >= 0; i--) {
            try {
                patterns.get(i).get(1);
            } catch (IndexOutOfBoundsException e) {
                patterns.remove(i);
            }
        }

        int mouseX = (int)Math.round(mX);
        int mouseY = (int)Math.round(mY);

        Pair<Long, Vector2i> closest = findClosestPointTo(mouseX, mouseY);
        long distance = closest.getLeft();
        Vector2i point = closest.getRight();

        if (
            distance <= distanceBeforeSnapToGridSqr &&
            !pointIsOccupied(point)
        ) currentlyEngaged.add(point);

        return true;
    }

    @Override
    public boolean mouseDragged(double mX, double mY, int button, double dX, double dY) {
        if (button != 0 || currentlyEngaged.size() == 0) return false;

        int mouseX = (int)Math.round(mX);
        int mouseY = (int)Math.round(mY);

        Pair<Long, Vector2i> closest = findClosestPointTo(mouseX, mouseY);
        long distance = closest.getLeft();
        Vector2i point = closest.getRight();

        Vector2i lastEngaged = currentlyEngaged.get(currentlyEngaged.size() - 1);
        if (
            distance <= distanceBeforeSnapToGridSqr &&
            lastEngaged.distanceSquared(mouseX, mouseY)
                <= distanceFromRecentPointBeforeStopSnappingSqr
        ) {
            if (
                currentlyEngaged.size() > 1 &&
                currentlyEngaged.get(currentlyEngaged.size() - 2).equals(point)
            )
                currentlyEngaged.remove(currentlyEngaged.size() - 1);

            else if (
                !lineIsOccupied(lastEngaged, point) &&
                !lastEngaged.equals(point)
            )
                currentlyEngaged.add(point);
        }


        return true;
    }

    @Override
    public boolean mouseReleased(double mX, double mY, int button) {
        if (button != 0 || currentlyEngaged.size() == 0) return false;

        // exception-based control flow :weedhexxy:
        try {
            currentlyEngaged.get(1);
            patterns.add(currentlyEngaged);
        } catch (IndexOutOfBoundsException e) {}

        currentlyEngaged = new ArrayList<>();

        return true;
    }

    private Map<String, Map<String, String>> getNextLetter = Map.of(
        "ne", Map.of(
            "ne", "w",
            "nw", "q",
            "w", "a",
            "sw", "s", // planned feature
            "se", "d",
            "e", "e"
        ),
        "nw", Map.of(
            "ne", "e",
            "nw", "w",
            "w", "q",
            "sw", "a",
            "se", "s",
            "e", "d"
        ),
        "w", Map.of(
            "ne", "d",
            "nw", "e",
            "w", "w",
            "sw", "q",
            "se", "a",
            "e", "s"
        ),
        "sw", Map.of(
            "ne", "s",
            "nw", "d",
            "w", "e",
            "sw", "w",
            "se", "q",
            "e", "a"
        ),
        "se", Map.of(
            "ne", "a",
            "nw", "s",
            "w", "d",
            "sw", "e",
            "se", "w",
            "e", "q"
        ),
        "e", Map.of(
            "ne", "q",
            "nw", "a",
            "w", "s",
            "sw", "d",
            "se", "e",
            "e", "w"
        )
    );

    private String getDirection(Vector2i from, Vector2i to) {
        if (to.x < from.x) {

            if (to.y < from.y) return "nw";
            else if (to.y > from.y) return "sw";
            else return "w";

        } else if (to.x > from.x) {

            if (to.y < from.y) return "ne";
            else if (to.y > from.y) return "se";
            else return "e";

        } else {

            // how does this happen, past me?
            if (to.y < from.y) return "n";
            else if (to.y > from.y) return "s";
            else throw new IllegalArgumentException("from and to are the same point. How did this happen?");

        }
    }

    private Map<String, HexDir> abbreviationToDir = Map.of(
        "ne", HexDir.NORTH_EAST,
        "nw", HexDir.NORTH_WEST,
        "w", HexDir.WEST,
        "sw", HexDir.SOUTH_WEST,
        "se", HexDir.SOUTH_EAST,
        "e", HexDir.EAST
    );

    private HexPattern serialize(List<Vector2i> patternPoints) {
        String firstDirection = getDirection(patternPoints.get(0), patternPoints.get(1));
        String previousDirection = firstDirection;
        String angleSig = "";

        Vector2i previous = patternPoints.get(1);
        for (Vector2i point : patternPoints.subList(2, patternPoints.size())) {

            String direction = getDirection(previous, point);
            angleSig += getNextLetter.get(previousDirection).get(direction);
            previousDirection = direction;
            previous = point;

        }

        return HexPattern.fromAngles(angleSig, abbreviationToDir.get(firstDirection));
    }

    @Override
    public void close() {

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(handler.getBlockPos());
        buf.writeInt(patterns.size());

        for (List<Vector2i> pattern : patterns.subList(0, patterns.size()))
            buf.writeNbt(serialize(pattern).serializeToNBT());

        ClientPlayNetworking.send(
            SET_PATTERNS_ON_CHALK,
            buf
        );

        super.close();

    }
}
