/**
 * 解析 JWT 中的 exp 欄位（秒），回傳毫秒時間戳
 */
export function getTokenExp(token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.exp * 1000
    } catch {
      return 0
    }
  }