import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const REPO_NAME = '/Dev-Web-Trabalho-Pratico-Parte-2/'

export default defineConfig({
  plugins: [react()],
  
  base: process.env.NODE_ENV === 'production' ? REPO_NAME : '/',
})
