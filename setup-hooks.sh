echo "
#!/bin/bash

./gradlew spotlessApply

if [ $? -ne 0 ]; then
  echo "❌ spotlessApply falhou. Commit cancelado."
  exit 1
fi

echo "✅ código formatado."

" > ./.git/hooks/pre-commit
chmod +x ./.git/hooks/pre-commit