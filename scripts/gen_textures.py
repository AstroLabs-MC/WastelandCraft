#!/usr/bin/env python3
"""
Generate placeholder textures for Wasteland mod blocks using only the Python standard library.
- Outputs 16x16 PNGs for:
  - assets/wasteland/textures/block/wasteland_block.png
  - assets/wasteland/textures/block/wasteland_dirt.png

Run:
  py -3 scripts/gen_textures.py
or
  python scripts/gen_textures.py

Then press F3+T in-game to reload resources.
"""
from __future__ import annotations
import os
import zlib
import struct
import random
from pathlib import Path
from typing import Tuple

RGBA = Tuple[int, int, int, int]

# --- Minimal PNG writer (RGBA8, no interlace, filter=0) ---

def _crc32(data: bytes) -> int:
    import binascii
    return binascii.crc32(data) & 0xFFFFFFFF


def _png_chunk(tag: bytes, data: bytes) -> bytes:
    return struct.pack(
        ">I", len(data)
    ) + tag + data + struct.pack(
        ">I", _crc32(tag + data)
    )


def write_png_rgba(path: Path, width: int, height: int, pixels: bytes) -> None:
    assert len(pixels) == width * height * 4, "pixels must be RGBA for every pixel"
    sig = b"\x89PNG\r\n\x1a\n"
    ihdr = struct.pack(
        ">IIBBBBB",
        width,
        height,
        8,  # bit depth
        6,  # color type RGBA
        0,  # compression
        0,  # filter
        0,  # interlace
    )
    # Prepend each scanline with filter=0 byte
    raw = bytearray()
    row_stride = width * 4
    for y in range(height):
        raw.append(0)
        start = y * row_stride
        raw.extend(pixels[start : start + row_stride])
    idat = zlib.compress(bytes(raw), level=9)
    png = bytearray()
    png += sig
    png += _png_chunk(b"IHDR", ihdr)
    png += _png_chunk(b"IDAT", idat)
    png += _png_chunk(b"IEND", b"")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(bytes(png))


# --- Helpers to build colors and tiles ---

def clamp(v: int, lo: int = 0, hi: int = 255) -> int:
    return lo if v < lo else hi if v > hi else v


def add_rgb(rgb: Tuple[int, int, int], dv: Tuple[int, int, int]) -> Tuple[int, int, int]:
    r, g, b = rgb
    dr, dg, db = dv
    return (clamp(r + dr), clamp(g + dg), clamp(b + db))


def lerp(a: float, b: float, t: float) -> float:
    return a + (b - a) * t


def lerp_rgb(a: Tuple[int, int, int], b: Tuple[int, int, int], t: float) -> Tuple[int, int, int]:
    return (
        int(lerp(a[0], b[0], t)),
        int(lerp(a[1], b[1], t)),
        int(lerp(a[2], b[2], t)),
    )


def make_value_noise(width: int, height: int, scale: int, rnd: random.Random) -> list[list[float]]:
    """Simple tileable value noise by interpolating a coarse grid."""
    gw, gh = max(1, width // scale), max(1, height // scale)
    grid = [[rnd.random() for _ in range(gw + 1)] for _ in range(gh + 1)]
    noise = [[0.0 for _ in range(width)] for _ in range(height)]
    for y in range(height):
        fy = (y / scale)
        y0 = int(fy) % gh
        y1 = (y0 + 1) % gh
        ty = fy - int(fy)
        for x in range(width):
            fx = (x / scale)
            x0 = int(fx) % gw
            x1 = (x0 + 1) % gw
            tx = fx - int(fx)
            # Bilinear
            v00 = grid[y0][x0]
            v10 = grid[y0][x1]
            v01 = grid[y1][x0]
            v11 = grid[y1][x1]
            nx0 = lerp(v00, v10, tx)
            nx1 = lerp(v01, v11, tx)
            noise[y][x] = lerp(nx0, nx1, ty)
    return noise


def pack_pixels(pixels: list[list[RGBA]], width: int, height: int) -> bytes:
    out = bytearray(width * height * 4)
    i = 0
    for y in range(height):
        row = pixels[y]
        for x in range(width):
            r, g, b, a = row[x]
            out[i] = r; out[i+1] = g; out[i+2] = b; out[i+3] = a
            i += 4
    return bytes(out)


def generate_dirt(size: int = 16, seed: int = 1337) -> bytes:
    rnd = random.Random(seed)
    base_lo = (0x4E, 0x43, 0x3A)  # dark brown
    base_hi = (0x7A, 0x66, 0x56)  # lighter brown
    n_low = make_value_noise(size, size, scale=6, rnd=rnd)
    n_high = make_value_noise(size, size, scale=3, rnd=rnd)
    pixels: list[list[RGBA]] = [[(0,0,0,255) for _ in range(size)] for _ in range(size)]
    for y in range(size):
        for x in range(size):
            t = 0.65 * n_low[y][x] + 0.35 * n_high[y][x]
            rgb = lerp_rgb(base_lo, base_hi, t)
            # Small speckles
            if rnd.random() < 0.06:
                d = rnd.randint(-18, -8)
                rgb = add_rgb(rgb, (d, d, d))
            pixels[y][x] = (rgb[0], rgb[1], rgb[2], 255)
    return pack_pixels(pixels, size, size)


def generate_stone(size: int = 16, seed: int = 4242) -> bytes:
    rnd = random.Random(seed)
    base_lo = (0x5E, 0x5E, 0x5E)
    base_hi = (0x78, 0x78, 0x78)
    n_low = make_value_noise(size, size, scale=7, rnd=rnd)
    n_high = make_value_noise(size, size, scale=3, rnd=rnd)
    pixels: list[list[RGBA]] = [[(0,0,0,255) for _ in range(size)] for _ in range(size)]
    for y in range(size):
        for x in range(size):
            t = 0.7 * n_low[y][x] + 0.3 * n_high[y][x]
            rgb = lerp_rgb(base_lo, base_hi, t)
            pixels[y][x] = (rgb[0], rgb[1], rgb[2], 255)
    # Add subtle diagonal streaks/cracks
    for _ in range(3):
        y = rnd.randrange(size)
        shade = rnd.randint(-25, -10)
        for x in range(size):
            yy = (y + (x // 3)) % size
            r,g,b,a = pixels[yy][x]
            r = clamp(r + shade); g = clamp(g + shade); b = clamp(b + shade)
            pixels[yy][x] = (r,g,b,a)
    return pack_pixels(pixels, size, size)


def main() -> None:
    root = Path(__file__).resolve().parents[1]
    out_dir = root / "src" / "main" / "resources" / "assets" / "wasteland" / "textures" / "block"
    out_dir.mkdir(parents=True, exist_ok=True)

    dirt_png = generate_dirt(16, seed=1337)
    stone_png = generate_stone(16, seed=4242)

    dirt_path = out_dir / "wasteland_dirt.png"
    stone_path = out_dir / "wasteland_block.png"

    write_png_rgba(dirt_path, 16, 16, dirt_png)
    write_png_rgba(stone_path, 16, 16, stone_png)

    print(f"Wrote: {dirt_path}")
    print(f"Wrote: {stone_path}")
    print("Done. Press F3+T in Minecraft to reload resources.")


if __name__ == "__main__":
    main()
