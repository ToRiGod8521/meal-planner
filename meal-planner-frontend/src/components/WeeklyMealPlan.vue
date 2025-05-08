<template>
    <div class="p-4">
    <h2 class="text-xl font-semibold mb-4">本週菜單</h2>

    <div v-if="errorMessage" class="mb-4 text-red-500">
        {{ errorMessage }}
    </div>

    <div class="mb-4">
    <button @click="generatePlan" :disabled="loading" class="mb-4 px-4 py-2 bg-blue-500 text-white rounded disabled:opacity-50">
    {{ loading ? '生成中…' : '生成本週菜單' }}
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
function mapPlansToWeek(plans) {
  const map = {};
  plans.forEach(p => {
    if (!map[p.planDate]) map[p.planDate] = { BREAKFAST: null, LUNCH: null, DINNER: null };
    const recipe = recipes.value[p.recipeId] || { name: '未知菜單' };
    map[p.planDate][p.mealType] = { id: p.recipeId, name: recipe.name };
  });
  return Object.entries(map).map(([date, meals]) => ({ date, meals }));
}
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
    const res = await axios.post('/api/mealplan/generate')
    weekDays.value = res.data
  } catch (err) {
    errorMessage.value = '生成菜單失敗'
    console.error(err)
  } finally {
    loading.value = false
  }
}
// format to 7×3 structure with labels
const formattedDays = computed(() => {
  if (!weekDays.value.length) return []
  // group by date
  const map = {}
  weekDays.value.forEach(p => {
    if (!map[p.planDate]) {
      map[p.planDate] = {
        date: p.planDate,
        label: formatDate(p.planDate),
        meals: { BREAKFAST: {}, LUNCH: {}, DINNER: {} }
      }
    }
    const mealInfo = recipes.value[p.recipeId] || {}
    map[p.planDate].meals[p.mealType] = {
      id: p.recipeId,
      name: mealInfo.name
    }
  })
  return Object.values(map).sort((a, b) => new Date(a.date) - new Date(b.date))
})

// date formatter
function formatDate(dateStr) {
  const d = new Date(dateStr)
  return new Intl.DateTimeFormat('zh-TW', {
    month: 'numeric',
    day: 'numeric',
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
