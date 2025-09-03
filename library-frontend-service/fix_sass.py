import re
import os

# Read the current scss file
scss_file = 'src/app/features/books/available-books/available-books.scss'
with open(scss_file, 'r') as f:
    content = f.read()

# 1. Replace rgba() calls with color.change()
def replace_rgba(match):
    rgba_content = match.group(1)
    parts = [part.strip() for part in rgba_content.split(',')]
    
    if len(parts) == 4:
        r, g, b, alpha = parts
        # Check if it's using variables
        if 'vars.$' in rgba_content:
            # Extract the variable name from the first part
            var_match = re.search(r'vars\.\$([\w-]+)', r)
            if var_match:
                return f'color.change(vars.${var_match.group(1)}, $alpha: {alpha})'
        
        # Handle regular color values
        if r.isdigit() and g.isdigit() and b.isdigit():
            hex_color = f"#{int(r):02x}{int(g):02x}{int(b):02x}"
            return f'color.change({hex_color}, $alpha: {alpha})'
        elif r.startswith('#'):
            return f'color.change({r}, $alpha: {alpha})'
    
    return match.group(0)  # Return original if we can't parse

# Replace all rgba() calls
content = re.sub(r'rgba\((.*?)\)', replace_rgba, content)

# 2. Fix mixed declarations by moving @include to the end of rules
# This is a complex fix for the specific case in the file
lines = content.split('\n')
fixed_lines = []

i = 0
while i < len(lines):
    line = lines[i].strip()
    
    # Special handling for the .available-books-container rule
    if '.available-books-container {' in line:
        fixed_lines.append(lines[i])  # Add the selector
        i += 1
        
        # Collect all property declarations
        properties = []
        includes = []
        
        while i < len(lines) and '}' not in lines[i]:
            current_line = lines[i]
            if '@include' in current_line:
                includes.append(current_line)
            else:
                properties.append(current_line)
            i += 1
        
        # Add properties first, then includes
        for prop in properties:
            fixed_lines.append(prop)
        for inc in includes:
            fixed_lines.append(inc)
        
        if i < len(lines):
            fixed_lines.append(lines[i])  # Add closing brace
    
    # Similar fix for .book-card rule
    elif '.book-card {' in line:
        fixed_lines.append(lines[i])  # Add the selector
        i += 1
        
        # Collect all property declarations
        properties = []
        includes = []
        nested_rules = []
        
        brace_count = 1
        while i < len(lines) and brace_count > 0:
            current_line = lines[i]
            
            if '{' in current_line:
                brace_count += current_line.count('{')
            if '}' in current_line:
                brace_count -= current_line.count('}')
            
            if brace_count == 1:  # Top level of .book-card
                if '@include' in current_line:
                    includes.append(current_line)
                elif current_line.strip().startswith('//') or current_line.strip().startswith('.') or current_line.strip().startswith('&') or '{' in current_line:
                    nested_rules.append(current_line)
                else:
                    properties.append(current_line)
            else:
                nested_rules.append(current_line)
            
            i += 1
            if brace_count == 0:
                nested_rules.append(current_line)  # Add closing brace
                break
        
        # Add properties first, then includes, then nested rules
        for prop in properties:
            if prop.strip() and not prop.strip().startswith('}'):
                fixed_lines.append(prop)
        for inc in includes:
            fixed_lines.append(inc)
        for rule in nested_rules[:-1]:  # All except the last closing brace
            fixed_lines.append(rule)
        if nested_rules:
            fixed_lines.append(nested_rules[-1])  # Add the closing brace
        
        i -= 1  # Adjust because we'll increment at the end of the loop
    
    # For card actions, fix the mixin order
    elif '.borrow-button {' in line or '.details-button {' in line:
        fixed_lines.append(lines[i])  # Add the selector
        i += 1
        
        properties = []
        includes = []
        
        while i < len(lines) and not (lines[i].strip() == '}' and lines[i-1].strip() != ''):
            current_line = lines[i]
            if '@include' in current_line:
                includes.append(current_line)
            else:
                properties.append(current_line)
            i += 1
        
        # Add includes first for buttons, then properties
        for inc in includes:
            fixed_lines.append(inc)
        for prop in properties:
            fixed_lines.append(prop)
        
        if i < len(lines):
            fixed_lines.append(lines[i])  # Add closing brace
    else:
        fixed_lines.append(lines[i])
    
    i += 1

# Write the fixed content back to the file
with open(scss_file, 'w') as f:
    f.write('\n'.join(fixed_lines))

print("Fixed SASS deprecation warnings in available-books.scss")
