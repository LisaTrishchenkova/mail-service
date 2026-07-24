#!/bin/bash

echo "🔍 Checking i18n files..."

I18N_DIR="src/main/resources/i18n"

if [ ! -d "$I18N_DIR" ]; then
  echo "❌ i18n directory not found: $I18N_DIR"
  exit 1
fi
echo "✅ i18n directory exists"

if [ ! -f "$I18N_DIR/messages.properties" ]; then
  echo "❌ messages.properties not found!"
  exit 1
fi
echo "✅ messages.properties exists"

if [ ! -f "$I18N_DIR/messages_zh_CN.properties" ]; then
  echo "❌ messages_zh_CN.properties not found!"
  exit 1
fi
echo "✅ messages_zh_CN.properties exists"

echo "🔍 Checking translations..."

extract_keys() {
  grep -v '^[[:space:]]*#' "$1" | grep -v '^[[:space:]]*$' | sed 's/=.*//' | sort -u
}

MISSING_KEYS=$(comm -23 \
  <(extract_keys "$I18N_DIR/messages.properties") \
  <(extract_keys "$I18N_DIR/messages_zh_CN.properties"))

if [ -n "$MISSING_KEYS" ]; then
  echo "$MISSING_KEYS" | while IFS= read -r k; do
    [ -n "$k" ] && echo "❌ Missing translation for: $k"
  done
  MISSING=$(printf '%s\n' "$MISSING_KEYS" | grep -c .)
  echo "❌ $MISSING keys are missing translations!"
  exit 1
fi
echo "✅ All keys are translated"

echo "🔍 Checking for empty values..."
EMPTY=0
while IFS='=' read -r key value; do
  if [[ -z "$key" || "$key" == \#* ]]; then
    continue
  fi
  if [[ -z "$value" || "$value" == " " ]]; then
    echo "❌ Empty value for key: $key"
    EMPTY=$((EMPTY + 1))
  fi
done < "$I18N_DIR/messages.properties"

if [ $EMPTY -gt 0 ]; then
  echo "❌ $EMPTY keys have empty values!"
  exit 1
fi
echo "✅ No empty values found"

TOTAL_KEYS=$(grep -v "^#" "$I18N_DIR/messages.properties" | grep -v "^$" | wc -l)
ZH_KEYS=$(grep -v "^#" "$I18N_DIR/messages_zh_CN.properties" | grep -v "^$" | wc -l)

echo ""
echo "📊 i18n Statistics:"
echo "  Total keys: $TOTAL_KEYS"
echo "  Chinese translations: $ZH_KEYS"
echo "  Translation coverage: $((ZH_KEYS * 100 / TOTAL_KEYS))%"

echo "✅ All i18n checks passed!"
