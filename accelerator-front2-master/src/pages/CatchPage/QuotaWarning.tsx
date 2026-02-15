import React from 'react'
import { Alert, Box, Typography, Collapse, Button } from '@mui/material'
import { Warning, Close } from '@mui/icons-material'
import { useState } from 'react'

const QuotaWarning: React.FC = () => {
	const [showWarning, setShowWarning] = useState(true)

	// В реальном приложении здесь будет логика проверки квот
	const hasWarning = false // временно отключим для демонстрации

	if (!hasWarning) return null

	return (
		<Collapse in={showWarning}>
			<Alert
				severity='warning'
				action={
					<Button
						color='inherit'
						size='small'
						onClick={() => setShowWarning(false)}
						startIcon={<Close />}
					>
						Скрыть
					</Button>
				}
				sx={{ mb: 2 }}
			>
				<Box>
					<Typography
						variant='subtitle1'
						gutterBottom
						sx={{ display: 'flex', alignItems: 'center', gap: 1 }}
					>
						<Warning />
						Внимание: приближается лимит квоты
					</Typography>
					<Typography variant='body2'>
						По кефали в Чёрном море осталось только 800 кг из 10 000 кг (92%
						использовано). Рекомендуется сократить вылов данного вида рыбы.
					</Typography>
				</Box>
			</Alert>
		</Collapse>
	)
}

export default QuotaWarning
