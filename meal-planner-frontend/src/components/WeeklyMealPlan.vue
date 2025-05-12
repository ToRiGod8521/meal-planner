<template>
    <div class="p-4">
    <h2 class="text-xl font-semibold mb-4">本週菜單</h2>

    <div v-if="errorMessage" class="mb-4 text-red-500">
        {{ errorMessage }}
    </div>

    <div class="mb-4 flex space-x-2">
    <button @click="generatePlan" :disabled="loading" class="mb-4 px-4 py-2 bg-blue-500 text-white rounded disabled:opacity-50">
    {{ loading ? '生成中…' : '生成本週菜單' }}
    </button>

    <button @click="downloadCsv" class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600">
      匯出購物清單CSV
    </button>

    <button @click="downloadPdf" class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">
      匯出購物清單 PDF
    </button>

    </div>

    <div>
      <button @click="simulateError" class="ml-4 px-2 py-1 bg-red-500 text-white rounded">模擬出錯
      </button>
    </div>

    <div v-if="loading && !weekDays.length">載入中…</div>

    <table v-if="!loading && weekDays.length" class="w-full table-auto border-collapse">
      <thead>
        <tr>
          <th class="border px-2 py-1">日期</th>
          <th class="border px-2 py-1">早餐</th>
          <th class="border px-2 py-1">午餐</th>
          <th class="border px-2 py-1">晚餐</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="day in formattedDays" :key="day.date" class="text-center">
          <td class="border px-2 py-1">{{ day.label }}</td>
          <td
            class="border px-2 py-1 cursor-pointer hover:bg-gray-100"
            @click="showDetail(day.meals.BREAKFAST)"
          >
            {{ day.meals.BREAKFAST.name || '—' }}
          </td>
          <td
            class="border px-2 py-1 cursor-pointer hover:bg-gray-100"
            @click="showDetail(day.meals.LUNCH)"
          >
            {{ day.meals.LUNCH.name || '—' }}
          </td>
          <td
            class="border px-2 py-1 cursor-pointer hover:bg-gray-100"
            @click="showDetail(day.meals.DINNER)"
          >
            {{ day.meals.DINNER.name || '—' }}
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50" @click.self="closeModal">
      <div class="bg-white p-6 rounded-lg max-w-lg w-full">
        <h3 class="text-lg font-bold mb-2">{{ selectedRecipe.name }}</h3>
        <p class="mb-2"><strong>Calories:</strong> {{ selectedRecipe.calories }}</p>
        <p class="mb-2"><strong>Protein:</strong> {{ selectedRecipe.protein }}</p>
        <div class="mb-4">
          <strong>Ingredients:</strong>
          <ul class="list-disc list-inside">
            <li v-for="(item, idx) in selectedRecipe.ingredients" :key="idx">{{ item }}</li>
          </ul>
        </div>
        <div class="mb-4">
          <strong>Instructions:</strong>
          <p>{{ selectedRecipe.instructions }}</p>
        </div>
        <button @click="closeModal" class="mt-4 px-4 py-2 bg-gray-500 text-white rounded">關閉</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted ,computed} from 'vue';
import axios from 'axios';

// data & state
const recipes = ref({});  // id → {name, calories, protein, ingredients, instructions}
const weekDays = ref([]);
const loading = ref(false);
const errorMessage = ref({});

// modal
const showModal = ref(false)
const selectedRecipe = ref({
  name: '',
  calories: null,
  protein: null,
  ingredients: [],
  instructions: ''
})

// X
// function mapPlansToWeek(plans) {
//   const map = {};
//   plans.forEach(p => {
//     if (!map[p.planDate]) map[p.planDate] = { BREAKFAST: null, LUNCH: null, DINNER: null };
//     const recipe = recipes.value[p.recipeId] || { name: '未知菜單' };
//     map[p.planDate][p.mealType] = { id: p.recipeId, name: recipe.name };
//   });
//   return Object.entries(map).map(([date, meals]) => ({ date, meals }));
// }
// load all recipes into a map
async function loadRecipes() {
  try {
    const res = await axios.get('/api/recipes')
    res.data.forEach(r => {
      recipes.value[r.id] = r
    })
  } catch (err) {
    errorMessage.value = '讀取食譜列表失敗'
    console.error(err)
  }
}
// load current meal plan
async function loadPlan() {
  loading.value = true
  errorMessage.value = null
  try {
    const res = await axios.get('/api/mealplan')
    weekDays.value = res.data
  } catch (err) {
    errorMessage.value = '讀取本週菜單失敗'
    console.error(err)
  } finally {
    loading.value = false
  }
}


// show detail modal
function showDetail(meal) {
  if (!meal.id) return
  const info = recipes.value[meal.id]
  selectedRecipe.value = {
    name: info.name,
    calories: info.calories,
    protein: info.protein,
    ingredients: JSON.parse(info.ingredients),
    instructions: info.instructions
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false;
}
//模擬錯誤發生
async function simulateError() {
  loading.value = true
  errorMessage.value = null
  try {
    //故意觸發不存在的endpoint，模擬404/500
    await axios.get('/api/does-not-exist')
  } catch (err) {
    errorMessage.value = '🔴 模擬錯誤發生：' + (err.response?.status || err.message)
    console.error(err)
  } finally {
    loading.value = false
  }
}

// generate new plan
async function generatePlan() {
  loading.value = true
  errorMessage.value = null

  await new Promise(r => setTimeout(r, 2000))

  try {
    const token = localStorage.getItem('jwt')
    const res = await axios.post('/api/mealplan/generate',{},{ headers: { Authorization: `Bearer ${token}` } })

    weekDays.value = res.data
  } catch (err) {
    if (err.response?.status === 403) {
      errorMessage.value = '權限不足，請重新登入'
      // redirect to login…
    } else {
      errorMessage.value = '生成菜單失敗'
    }
    console.error(err)
  } finally {
    loading.value = false
  }
}
// // format to 7×3 structure with labels
// const formattedDays = computed(() => {
//   if (!weekDays.value.length) return []
//   // group by date
//   const map = {}
//   weekDays.value.forEach(p => {
//     if (!map[p.planDate]) {
//       map[p.planDate] = {
//         date: p.planDate,
//         label: formatDate(p.planDate),
//         meals: { BREAKFAST: {}, LUNCH: {}, DINNER: {} }
//       }
//     }
//     const mealInfo = recipes.value[p.recipeId] || {}
//     map[p.planDate].meals[p.mealType] = {
//       id: p.recipeId,
//       name: mealInfo.name
//     }
//   })
//   return Object.values(map).sort((a, b) => new Date(a.date) - new Date(b.date))
// })
const formattedDays = computed(() => {
  // 1. 先算出本週週一
  const today = new Date()
  const dow   = today.getDay()  // 0=日,1=一…6=六
  const monday = new Date(today)
  if (dow === 0) {
    monday.setDate(today.getDate() - 6)  // 如果今天是週日
  } else {
    monday.setDate(today.getDate() - (dow - 1))
  }

  // 2. 生成週一 ~ 週日的七筆預設資料
  const result = []
  for (let i = 0; i < 7; i++) {
    const d = new Date(monday)
    d.setDate(monday.getDate() + i)
    // const iso = d.toISOString().split('T')[0]  // YYYY-MM-DD
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const dd= String(d.getDate()).padStart(2, '0');
    const iso = `${y}-${m}-${dd}`

    // 三餐預設為空
    const meals = {
      BREAKFAST: { id: null, name: '—' },
      LUNCH:     { id: null, name: '—' },
      DINNER:    { id: null, name: '—' }
    }

    // 3. 把後端回傳的 weekDays 塞進來
    weekDays.value.forEach(p => {
      if (p.planDate === iso && meals[p.mealType]) {
        const info = recipes.value[p.recipeId] || {}
        meals[p.mealType] = {
          id:   p.recipeId,
          name: info.name || '未知菜單'
        }
      }
    })

    result.push({
      date:  iso,
      label: formatDate(iso),
      meals
    })
  }

  return result
})

/** 下載本週購物清單 CSV **/
async function downloadCsv() {
  try {
    const res = await axios.get('/api/shopping-list/export/csv', {
      responseType: 'blob'
    })
    const blob = new Blob([res.data], { type: 'text/csv;charset=utf-8;' })
    const url  = URL.createObjectURL(blob)
    const a    = document.createElement('a')
    a.href     = url
    a.download = 'shopping-list.csv'
    a.click()
    URL.revokeObjectURL(url)
  } catch (err) {
    console.error('下載 CSV 失敗', err)
    alert('下載 CSV 失敗')
  }
}
/** 下載本週購物清單 PDF **/
async function downloadPdf() {
  try {
    const res = await axios.get('/api/shopping-list/export/pdf', {
      responseType: 'blob'
    })
    const blob = new Blob([res.data], { type: 'application/pdf' })
    const url  = URL.createObjectURL(blob)
    const a    = document.createElement('a')
    a.href     = url
    a.download = 'shopping-list.pdf'
    a.click()
    URL.revokeObjectURL(url)
  } catch (err) {
    console.error('下載 PDF 失敗', err)
    alert('下載 PDF 失敗')
  }
}

// date formatter
function formatDate(dateStr) {
  const d = new Date(dateStr)
  // 如果不是合法日期，就直接回传原始（或空字串）
  if (isNaN(d.getTime())) {
    return dateStr || ''
  }
  return new Intl.DateTimeFormat('zh-TW', {
    month:   'numeric',
    day:     'numeric',
    weekday: 'short'
  }).format(d)
}
// on mount load data
onMounted(async () => {
  await loadRecipes();
  await loadPlan();
});
</script>

<style scoped>
.cursor-pointer { cursor: pointer; }
.hover\:bg-gray-100:hover { background-color: #f7fafc; }
</style>
