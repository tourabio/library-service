import re

scss_file = 'src/app/features/books/available-books/available-books.scss'

# Read the backup file to start fresh
with open(scss_file + '.backup', 'r') as f:
    content = f.read()

# Step 1: Replace all rgba() calls
def replace_rgba_advanced(content):
    patterns = [
        # rgba(vars.$var, alpha)
        (r'rgba\((vars\.\$[\w-]+),\s*([\d.]+)\)', r'color.change(\1, $alpha: \2)'),
        # rgba(#hex, alpha)  
        (r'rgba\((#[0-9a-fA-F]{6}),\s*([\d.]+)\)', r'color.change(\1, $alpha: \2)'),
        # rgba(r, g, b, alpha) - convert to hex
        (r'rgba\((\d+),\s*(\d+),\s*(\d+),\s*([\d.]+)\)', lambda m: f'color.change(#{int(m.group(1)):02x}{int(m.group(2)):02x}{int(m.group(3)):02x}, $alpha: {m.group(4)})'),
    ]
    
    for pattern, replacement in patterns:
        if callable(replacement):
            content = re.sub(pattern, replacement, content)
        else:
            content = re.sub(pattern, replacement, content)
    
    return content

content = replace_rgba_advanced(content)

# Step 2: Fix the .available-books-container mixed declarations
content = content.replace(
    """.available-books-container {
  @include mixins.library-container;
  padding: vars.$spacing-xl vars.$spacing-xl;
  min-height: 100vh;
  background: linear-gradient(
    135deg,
    color.change(vars.$library-warm-beige, $alpha: 0.1) 0%,
    color.change(vars.$library-light-brown, $alpha: 0.05) 50%,
    color.change(vars.$library-accent-gold, $alpha: 0.03) 100%
  );
}""",
    """.available-books-container {
  padding: vars.$spacing-xl vars.$spacing-xl;
  min-height: 100vh;
  background: linear-gradient(
    135deg,
    color.change(vars.$library-warm-beige, $alpha: 0.1) 0%,
    color.change(vars.$library-light-brown, $alpha: 0.05) 50%,
    color.change(vars.$library-accent-gold, $alpha: 0.03) 100%
  );

  @include mixins.library-container;
}"""
)

# Step 3: Fix the .book-card mixed declarations
book_card_section = """// Book Card
.book-card {
  @include mixins.library-card(false, false);
  position: relative;
  overflow: hidden;
  background: color.change(#ffffff, $alpha: 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid color.change(vars.$library-light-brown, $alpha: 0.1);
  transition: all vars.$transition-normal cubic-bezier(0.4, 0, 0.2, 1);
  height: 100%;
  display: flex;
  flex-direction: column;

  // Card Glow Effect
  .card-glow {
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(
      circle,
      color.change(vars.$library-accent-gold, $alpha: 0.1) 0%,
      transparent 70%
    );
    opacity: 0;
    transition: all vars.$transition-slow;
    pointer-events: none;
    z-index: -1;
  }

  // Availability variants
  &.availability-high {
    border-left: 4px solid #4CAF50;
  }

  &.availability-medium {
    border-left: 4px solid #FF9800;
  }

  &.availability-low {
    border-left: 4px solid #F44336;
  }
}"""

# Replace the problematic book-card section
content = re.sub(
    r'// Book Card.*?^}',
    book_card_section,
    content,
    flags=re.DOTALL | re.MULTILINE
)

# Step 4: Fix button sections
button_section = """.borrow-button {
    flex: 1;
    max-width: 160px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: vars.$spacing-xs;

    @include mixins.library-button(primary, medium);

    .mat-icon {
      font-size: 1.2rem;
    }
  }

  .details-button {
    min-width: 100px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: vars.$spacing-xs;

    @include mixins.library-button(secondary, medium);

    .mat-icon {
      font-size: 1.1rem;
    }
  }"""

content = re.sub(
    r'\.borrow-button \{.*?\.mat-icon \{\s*font-size: 1\.1rem;\s*\}\s*\}',
    button_section,
    content,
    flags=re.DOTALL
)

# Write the completely fixed file
with open(scss_file, 'w') as f:
    f.write(content)

print("Completely fixed all SASS issues in available-books.scss")
