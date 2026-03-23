FROM mcr.microsoft.com/devcontainers/base:ubuntu-22.04

# 使用中科大镜像源（自动适配ARM/x86，只替换域名不改变路径）
RUN sed -i 's|http://[^/]*/|http://mirrors.ustc.edu.cn/|g' /etc/apt/sources.list

# Install additional packages
RUN apt-get update && apt-get install -y \
    curl \
    git \
    vim \
    && rm -rf /var/lib/apt/lists/*

# Create vscode user (if not exists)
RUN useradd -m -s /bin/bash vscode || true
