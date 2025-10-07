# Sleepy — make `sleepy` a terminal command

A short README-style guide that takes you from `Sleepy.java` → compiled → runnable on the terminal as the command `sleepy`. Two approaches are shown:

- **Recommended (portable)**: build a runnable JAR and a small wrapper script.
- **Simple (no JAR)**: run the `.class` directly via `java`.

Pick one. All commands assume a Unix-like system with `zsh` (macOS / Linux). Replace `/full/path/to/...` or `$(pwd)` as appropriate.

---

## Quickstart (one-liner)
If you want the minimal set of commands to copy into a terminal (runnable-jar approach):

```zsh
# in project dir (where Sleepy.java lives)
javac Sleepy.java
jar cfe Sleepy.jar Sleepy Sleepy.class
mkdir -p ~/bin
echo '#!/usr/bin/env zsh' > ~/bin/sleepy
echo 'exec java -jar "'"$(pwd)/Sleepy.jar"'" "$@"' >> ~/bin/sleepy
chmod +x ~/bin/sleepy
# ensure ~/bin is in your PATH (see below)
```

---

## 1) Prerequisites
- JDK installed (`javac`, `java`, `jar` available). Check with:
  ```zsh
  java -version
  javac -version
  ```
- `~/bin` should be on your `PATH`. If not, add to `~/.zshrc`:
  ```zsh
  mkdir -p ~/bin
  echo 'export PATH="$HOME/bin:$PATH"' >> ~/.zshrc
  source ~/.zshrc
  ```

---

## 2) Example `Sleepy.java`
(so the README is self-contained — your real file may differ)

```java
// Sleepy.java
public class Sleepy {
    public static void main(String[] args) {
        System.out.println("I'm sleepy... zzz");
        for (String a : args) System.out.println("arg: " + a);
    }
}
```

---

## 3) Build steps

### A — Compile
From the directory containing `Sleepy.java`:

```zsh
javac Sleepy.java
# produces Sleepy.class (and any other classes)
```

### B — Make a runnable JAR (recommended)
Two equivalent ways:

**Auto-generate manifest with `-e`:**

```zsh
jar cfe Sleepy.jar Sleepy Sleepy.class
```

**Create manifest file manually:**

`manifest.txt`
```
Main-Class: Sleepy
```
(make sure there is a newline at the end)

Then:

```zsh
jar cfm Sleepy.jar manifest.txt Sleepy.class
```

**Notes**
- If your class is inside a package, use the fully-qualified class name: e.g. `jar cfe Sleepy.jar com.example.Sleepy path/to/classes`.
- The manifest line `Main-Class: Sleepy` tells `java -jar Sleepy.jar` which class’s `main()` to run.

---

## 4) Install a `sleepy` command (two options)

### Option 1 — Wrapper script that runs the JAR (recommended)
1. Create `~/bin` (if needed) and copy/move the JAR somewhere permanent. Example: keep JAR in the project and reference an absolute path, or move it to `~/lib/`:

```zsh
mkdir -p ~/bin
# optional: move jar to ~/lib
mkdir -p ~/lib
mv Sleepy.jar ~/lib/Sleepy.jar            # optional
```

2. Create the wrapper script `~/bin/sleepy`:

```zsh
cat > ~/bin/sleepy <<'EOF'
#!/usr/bin/env zsh
# Wrapper: run sleepy's runnable JAR
exec java -jar "$HOME/lib/Sleepy.jar" "$@"
EOF
```

If you left the JAR in the repo dir, you can use the project absolute path instead of `$HOME/lib/Sleepy.jar`. Example using current dir:

```zsh
exec java -jar "$(pwd)/Sleepy.jar" "$@"
```

3. Make it executable:

```zsh
chmod +x ~/bin/sleepy
```

4. Ensure `~/bin` is on your `PATH` (see Prerequisites). Verify:

```zsh
which sleepy     # should print ~/bin/sleepy
sleepy arg1 arg2 # runs the program
```

### Option 2 — Wrapper script that runs the `.class` directly (no JAR)
Use this if you prefer just compiled classes:

```zsh
# keep classes in ~/projects/sleepy or similar
# create wrapper
cat > ~/bin/sleepy <<'EOF'
#!/usr/bin/env zsh
# run Sleepy.class from a classpath
exec java -cp /full/path/to/project Sleepy "$@"
EOF

chmod +x ~/bin/sleepy
```

Replace `/full/path/to/project` with the directory containing `Sleepy.class`. This runs `java -cp <dir> Sleepy`.

---

## 5) Usage

```zsh
sleepy                # runs with no args
sleepy hello world    # passes args -> visible inside Java as args[]
```

---

## 6) Troubleshooting & tips

- **`java: command not found`** — install a JDK and/or ensure `java` is on your `PATH`.
- **`which sleepy` returns nothing** — make sure `~/bin` is in `$PATH` and you've `source ~/.zshrc` (or open a new terminal).
- **Permissions** — `chmod +x ~/bin/sleepy` is required.
- **If Sleepy is in a package** (e.g. `package com.example;`) then:
  - Use the fully-qualified main class in the manifest: `Main-Class: com.example.Sleepy`
  - Or call with `java -cp . com.example.Sleepy`.
- **Multiple class files** — include all `.class` files in the JAR (e.g. `jar cfe Sleepy.jar Sleepy *.class`) or build with a build tool (Maven/Gradle) and create a "fat jar" if you have external dependencies.
- **Manifest newline** — the `MANIFEST.MF` must end in a newline. `jar cfe` handles this for you.

---

## 7) Optional: Makefile (build + install)
Drop this in your repo for convenience:

```makefile
JAR=Sleepy.jar
MAIN=Sleepy
OUTDIR=out
INSTALL_DIR=$(HOME)/lib
BIN_DIR=$(HOME)/bin

all: compile jar

compile:
	javac -d $(OUTDIR) Sleepy.java

jar: compile
	jar cfe $(JAR) $(MAIN) -C $(OUTDIR) .

install: jar
	mkdir -p $(INSTALL_DIR) $(BIN_DIR)
	mv $(JAR) $(INSTALL_DIR)/$(JAR)
	cat > $(BIN_DIR)/sleepy <<'EOF'
#!/usr/bin/env zsh
exec java -jar "$(INSTALL_DIR)/$(JAR)" "$@"
EOF
	chmod +x $(BIN_DIR)/sleepy

.PHONY: all compile jar install
```

(Replace `INSTALL_DIR` usage inside the heredoc appropriately if you want literal variables.)

---

## That's it!
This README contains everything to compile `Sleepy.java`, package it (if desired), and create a `sleepy` command available from your shell.
